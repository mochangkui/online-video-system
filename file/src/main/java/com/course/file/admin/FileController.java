package com.course.file.admin;

import com.course.server.dto.FileDto;
import com.course.server.dto.PageDto;
import com.course.server.dto.ResponseDto;
import com.course.server.service.FileService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/file")
public class FileController {
    private static final Logger LOG = LoggerFactory.getLogger(FileController.class);
    public static final String BUSINESS_NAME = "文件";

    @Resource
    private FileService fileService;

    /**
     * 列表查询
     */
    @PostMapping("/list")
    public ResponseDto<PageDto<FileDto>> list(@RequestBody PageDto<FileDto> pageDto) {
        ResponseDto<PageDto<FileDto>> responseDto = new ResponseDto<>();
        fileService.list(pageDto);
        responseDto.setContent(pageDto);
        return responseDto;
    }
}