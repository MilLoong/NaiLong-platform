package com.nailong.service.impl;

import cn.hutool.core.bean.BeanUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nailong.common.constant.RedisKey;
import com.nailong.common.enums.ResultCode;
import com.nailong.common.exception.BusinessException;
import com.nailong.mapper.NoticeMapper;
import com.nailong.mapper.UserMapper;
import com.nailong.model.dto.notice.NoticeCreateDTO;
import com.nailong.model.dto.notice.NoticeUpdateDTO;
import com.nailong.model.entity.Notice;
import com.nailong.model.entity.User;
import com.nailong.model.vo.notice.NoticeVO;
import com.nailong.service.INoticeService;
import com.nailong.service.impl.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @brief 公告服务实现
 * @details 提供公告 CRUD、发布与 Redis 缓存
 * @author Nailong
 */
@Slf4j
@Service
public class NoticeServiceImpl extends ServiceImpl<NoticeMapper, Notice> implements INoticeService {

    private final StringRedisTemplate stringRedisTemplate;
    private final NoticeMapper noticeMapper;
    private final ObjectMapper objectMapper;
    private final UserMapper userMapper;

    private static final long CACHE_EXPIRE_TIME = 3600;

    public NoticeServiceImpl(StringRedisTemplate stringRedisTemplate, NoticeMapper noticeMapper, ObjectMapper objectMapper, UserMapper userMapper) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.noticeMapper = noticeMapper;
        this.objectMapper = objectMapper;
        this.userMapper = userMapper;
    }

    /**
     * @brief 创建公告
     * @param dto 创建请求
     * @return 新公告 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createNotice(NoticeCreateDTO dto) {
        Long publisherId = getCurrentUserId();

        Notice notice = new Notice();
        BeanUtil.copyProperties(dto, notice);
        notice.setPublisherId(publisherId);
        notice.setViewCount(0);

        if (dto.getStatus() == 1) {
            notice.setPublishTime(LocalDateTime.now());
        }

        noticeMapper.insert(notice);
        clearNoticeCache();

        log.info("创建公告成功: id={}, title={}, publisherId={}",
                 notice.getId(), notice.getTitle(), publisherId);

        return notice.getId();
    }

    /**
     * @brief 更新公告
     * @param dto 更新请求
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateNotice(NoticeUpdateDTO dto) {
        Notice notice = noticeMapper.selectById(dto.getId());
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        BeanUtil.copyProperties(dto, notice);

        if (notice.getStatus() == 1 && notice.getPublishTime() == null) {
            notice.setPublishTime(LocalDateTime.now());
        }

        noticeMapper.updateById(notice);
        clearNoticeCache();

        log.info("更新公告成功: id={}, title={}", notice.getId(), notice.getTitle());
    }

    /**
     * @brief 删除公告
     * @param id 公告 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        noticeMapper.deleteById(id);
        clearNoticeCache();

        log.info("删除公告成功: id={}, title={}", id, notice.getTitle());
    }

    /**
     * @brief 发布公告
     * @param id 公告 ID
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void publishNotice(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        if (notice.getStatus() == 1) {
            return;
        }

        int result = noticeMapper.publishNotice(id);
        if (result == 0) {
            throw new BusinessException("公告发布失败");
        }

        clearNoticeCache();

        log.info("发布公告成功: id={}, title={}", id, notice.getTitle());
    }

    /**
     * @brief 获取公告详情
     * @param id 公告 ID
     * @return 公告 VO
     * @details 访问时自动增加浏览次数
     */
    @Override
    public NoticeVO getNoticeDetail(Long id) {
        Notice notice = noticeMapper.selectById(id);
        if (notice == null || notice.getStatus() == 0) {
            throw new BusinessException(ResultCode.NOTICE_NOT_FOUND);
        }

        NoticeVO vo = BeanUtil.copyProperties(notice, NoticeVO.class);
        incrementViewCount(id);

        return vo;
    }

    /**
     * @brief 获取已发布公告列表
     * @return 公告 VO 列表（按置顶与发布时间排序）
     * @details 优先从 Redis 缓存读取
     */
    @Override
    public List<NoticeVO> getPublishedNoticeList() {
        try {
            String cacheKey = RedisKey.NOTICE_LIST;
            List<NoticeVO> cachedList = getListFromCache(cacheKey, NoticeVO.class);
            if (cachedList != null) {
                return cachedList;
            }
        } catch (Exception e) {
            log.error("从缓存获取公告列表失败", e);
        }

        List<Notice> noticeList = noticeMapper.selectPublishedList();

        if (CollectionUtils.isEmpty(noticeList)) {
            return new ArrayList<>();
        }

        List<NoticeVO> voList = noticeList.stream()
                .map(notice -> BeanUtil.copyProperties(notice, NoticeVO.class))
                .collect(Collectors.toList());

        try {
            String cacheKey = RedisKey.NOTICE_LIST;
            putListToCache(cacheKey, voList, CACHE_EXPIRE_TIME, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.error("缓存公告列表失败", e);
        }

        return voList;
    }

    /**
     * @brief 获取全部公告列表（含未发布）
     * @return 公告 VO 列表
     */
    @Override
    public List<NoticeVO> getAllNoticeList() {
        List<Notice> noticeList = noticeMapper.selectAllList();

        if (CollectionUtils.isEmpty(noticeList)) {
            return new ArrayList<>();
        }

        return noticeList.stream()
                .map(notice -> BeanUtil.copyProperties(notice, NoticeVO.class))
                .collect(Collectors.toList());
    }

    /**
     * @brief 增加公告浏览次数
     * @param id 公告 ID
     */
    @Override
    public void incrementViewCount(Long id) {
        try {
            noticeMapper.incrementViewCount(id);
        } catch (Exception e) {
            log.error("增加公告浏览次数失败: id={}, error={}", id, e.getMessage());
        }
    }

    /**
     * @brief 获取当前登录用户 ID
     * @return 用户 ID
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || authentication.getPrincipal() instanceof String) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }

        if (authentication.getPrincipal() instanceof Long) {
            return (Long) authentication.getPrincipal();
        }

        if (authentication.getPrincipal() instanceof UserDetails) {
            String username = ((UserDetails) authentication.getPrincipal()).getUsername();
            try {
                LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
                wrapper.eq(User::getUsername, username);
                User user = userMapper.selectOne(wrapper);
                if (user == null) {
                    throw new BusinessException(ResultCode.USER_NOT_FOUND);
                }
                return user.getId();
            } catch (Exception e) {
                log.error("获取用户信息失败", e);
                throw new BusinessException(ResultCode.UNAUTHORIZED);
            }
        }
        throw new BusinessException(ResultCode.UNAUTHORIZED);
    }

    /**
     * @brief 清除公告列表缓存
     */
    private void clearNoticeCache() {
        stringRedisTemplate.delete(RedisKey.NOTICE_LIST);
    }

    /**
     * @brief 从 Redis 读取列表缓存
     * @param key         缓存键
     * @param elementType 元素类型
     * @return 列表，缓存不存在时返回 null
     */
    private <T> List<T> getListFromCache(String key, Class<T> elementType) {
        try {
            String json = stringRedisTemplate.opsForValue().get(key);
            if (json == null || json.isEmpty()) {
                return null;
            }

            JsonNode jsonNode = objectMapper.readTree(json);
            List<T> list = new ArrayList<>();

            if (jsonNode.isArray()) {
                for (JsonNode node : jsonNode) {
                    list.add(objectMapper.treeToValue(node, elementType));
                }
            }

            return list;
        } catch (Exception e) {
            log.error("从缓存获取列表失败: key={}", key, e);
            return null;
        }
    }

    /**
     * @brief 将列表写入 Redis 缓存
     * @param key      缓存键
     * @param list     列表数据
     * @param timeout  过期时长
     * @param timeUnit 时间单位
     */
    private <T> void putListToCache(String key, List<T> list, long timeout, TimeUnit timeUnit) {
        try {
            String json = objectMapper.writeValueAsString(list);
            stringRedisTemplate.opsForValue().set(key, json, timeout, timeUnit);
        } catch (Exception e) {
            log.error("缓存列表数据失败: key={}", key, e);
        }
    }
}
