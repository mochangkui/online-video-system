package com.course.business.controller.admin;

import com.course.server.dto.ChapterDto;
import com.course.server.dto.PageDto;
import com.course.server.dto.ResponseDto;
import com.course.server.service.ChapterService;
import com.course.server.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin")
public class ChapterController {

    private static final Logger LOG = LoggerFactory.getLogger(ChapterController.class);
    private static final String BUSINESS_NAME = "大章";

    @Resource
    private ChapterService chapterService;

    /**
     * 查询列表
     * @param pageDto 分页数据
     * @return 数据
     */
    @RequestMapping("/list")
    public ResponseDto<PageDto<ChapterDto>> list(@RequestBody PageDto<ChapterDto> pageDto) {
        LOG.info("pageDto: {}", pageDto);
        ResponseDto<PageDto<ChapterDto>> responseDto = new ResponseDto<>();
        chapterService.list(pageDto);
        responseDto.setContent(pageDto);
        return responseDto;
    }

    /**
     * 新增大章数据
     * @param chapterDto 大章数据
     * @return 大章数据
     */
    @RequestMapping("/save")
    public ResponseDto<ChapterDto> list(@RequestBody ChapterDto chapterDto) {
        LOG.info("chapterDto: {}", chapterDto);

        // 保存校验
        ValidatorUtil.require(chapterDto.getName(), "名称");
        ValidatorUtil.require(chapterDto.getCourseId(), "课程ID");
        ValidatorUtil.length(chapterDto.getCourseId(), "课程ID", 1, 8);

        ResponseDto<ChapterDto> responseDto = new ResponseDto<>();
        chapterService.save(chapterDto);
        responseDto.setContent(chapterDto);
        return responseDto;
    }

    /**
     * 删除大章
     * @param id 大章id
     * @return ResponseDto
     */
    @DeleteMapping("/delete{id}")
    public ResponseDto<Object> delete(@PathVariable String id) {
        LOG.info("id: {}", id);
        ResponseDto<Object> responseDto = new ResponseDto<>();
        chapterService.delete(id);
        return responseDto;
    }
}
