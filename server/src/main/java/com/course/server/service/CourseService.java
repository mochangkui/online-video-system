package com.course.server.service;

import com.course.server.domain.Course;
import com.course.server.domain.CourseContent;
import com.course.server.dto.CourseContentDto;
import com.course.server.dto.CourseDto;
import com.course.server.dto.CoursePageDto;
import com.course.server.dto.SortDto;
import com.course.server.mapper.CourseContentMapper;
import com.course.server.mapper.CourseMapper;
import com.course.server.mapper.my.MyCourseMapper;
import com.course.server.util.UuidUtil;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Service
public class CourseService {
    private static final Logger LOG = LoggerFactory.getLogger(CourseService.class);
    @Resource
    private CourseMapper courseMapper;

    @Resource
    private MyCourseMapper myCourseMapper;

    @Resource
    private CourseContentMapper courseContentMapper;

    @Resource
    private CourseCategoryService courseCategoryService;

    /**
     * 列表查询：关联课程分类表
     * @param coursePageDto
     */
    public void list(CoursePageDto coursePageDto) {
        PageHelper.startPage(coursePageDto.getPage(), coursePageDto.getSize());
        List<CourseDto> courseDtoList = myCourseMapper.list(coursePageDto);
        PageInfo<CourseDto> pageInfo = new PageInfo<>(courseDtoList);
        coursePageDto.setTotal(pageInfo.getTotal());
        coursePageDto.setList(courseDtoList);
    }

    /**
     * 保存，id有值时更新，无值时新增
     */
    @Transactional
    public void save(CourseDto courseDto) {
        Course course = new Course();
        BeanUtils.copyProperties(courseDto, course);
        if (StringUtils.isEmpty(courseDto.getId())) {
            this.insert(course);
        } else {
            this.update(course);
        }

        // 批量保存课程分类
//        courseCategoryService.saveBatch(course.getId(), courseDto.getCategorys());
    }

    /**
     * 新增
     */
    private void insert(Course course) {
        Date now = new Date();
        course.setCreatedAt(now);
        course.setUpdatedAt(now);
        course.setId(UuidUtil.getShortUuid());
        courseMapper.insert(course);
    }

    /**
     * 更新
     */
    private void update(Course course) {
        course.setUpdatedAt(new Date());
        courseMapper.updateByPrimaryKey(course);
    }

    /**
     * 删除
     */
    public void delete(String id) {
        courseMapper.deleteByPrimaryKey(id);
    }

    /**
     * 更新课程时长
     * @param courseId
     */
    public void updateTime(String courseId) {
        LOG.info("更新课程时长：{}", courseId);
        myCourseMapper.updateTime(courseId);
    }

    /**
     * 查找课程内容
     */
    public CourseContentDto findContent(String id) {
        CourseContent content = courseContentMapper.selectByPrimaryKey(id);
        if (content == null) {
            return null;
        }
        CourseContentDto courseContentDto = new CourseContentDto();
        BeanUtils.copyProperties(content, courseContentDto);
        return courseContentDto;
    }

    /**
     * 保存课程内容，包含新增和修改
     */
    public int saveContent(CourseContentDto contentDto) {
        CourseContent courseContent = new CourseContent();
        BeanUtils.copyProperties(contentDto, courseContent);
        int i = courseContentMapper.updateByPrimaryKeyWithBLOBs(courseContent);
        if (i == 0) {
            i = courseContentMapper.insert(courseContent);
        }
        return i;
    }

    /**
     * 排序
     * @param sortDto
     */
    @Transactional
    public void sort(SortDto sortDto) {
        // 修改当前记录的排序值
        myCourseMapper.updateSort(sortDto);

        // 如果排序值变大
        if (sortDto.getNewSort() > sortDto.getOldSort()) {
            myCourseMapper.moveSortsForward(sortDto);
        }

        // 如果排序值变小
        if (sortDto.getNewSort() < sortDto.getOldSort()) {
            myCourseMapper.moveSortsBackward(sortDto);
        }
    }
}
