package com.sky.controller.admin;

import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 通用接口
 */
@RestController//表明在框架中的作用
@RequestMapping("/admin/common")
@Api(tags = "通用接口")
@Slf4j
public class CommonController {
    @Autowired //创建好后自动装填
    private AliOssUtil aliOssUtil;
    @PostMapping("upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file){//用SpringMVC框架封装的文件类型,声明为Multi.File ,注意参数名file和前端保持一致
        //返回值String表示文件的存储路径
        log.info("文件上传:{}",file);

        try {

            //原始文件名
            String originFileName = file.getOriginalFilename();
            //截取原始文件名的后缀
            String extension =originFileName.substring(originFileName.lastIndexOf("."));//从最后一个.开始截取出后缀名
            //构造新文件名称
            String objectName= UUID.randomUUID().toString() + extension;


            String filePath = aliOssUtil.upload(file.getBytes(), objectName);
            return Result.success(filePath);

        } catch (IOException e) {
            log.error("文件上传失败");
            throw new RuntimeException(e);
        }

    }
}
