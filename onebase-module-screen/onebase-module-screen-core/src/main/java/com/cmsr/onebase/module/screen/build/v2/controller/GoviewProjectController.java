package com.cmsr.onebase.module.screen.build.v2.controller;

import com.cmsr.onebase.module.screen.build.v2.common.base.BaseController;
import com.cmsr.onebase.module.screen.build.v2.common.config.V2Config;
import com.cmsr.onebase.module.screen.build.v2.common.domain.AjaxResult;
import com.cmsr.onebase.module.screen.build.v2.common.domain.ResultTable;
import com.cmsr.onebase.module.screen.build.v2.common.domain.Tablepar;
import com.cmsr.onebase.module.screen.build.v2.model.GoviewProject;
import com.cmsr.onebase.module.screen.build.v2.model.GoviewProjectData;
import com.cmsr.onebase.module.screen.build.v2.model.SysFile;
import com.cmsr.onebase.module.screen.build.v2.model.vo.GoviewProjectVo;
import com.cmsr.onebase.module.screen.build.v2.model.vo.SysFileVo;
import com.cmsr.onebase.module.screen.build.v2.service.IGoviewProjectDataService;
import com.cmsr.onebase.module.screen.build.v2.service.IGoviewProjectService;
import com.cmsr.onebase.module.screen.build.v2.service.ISysFileService;
import com.cmsr.onebase.module.screen.build.v2.util.ConvertUtil;
import com.cmsr.onebase.module.screen.build.v2.util.SnowflakeIdWorker;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author fc
 * @since 2023-04-30
 */
@RestController
@RequestMapping("/dashboard")
public class GoviewProjectController  extends BaseController {
	@Autowired
	private ISysFileService iSysFileService;
	@Autowired
	private V2Config v2Config;
	@Autowired
	private IGoviewProjectService iGoviewProjectService;
	@Autowired
	private IGoviewProjectDataService iGoviewProjectDataService;


	@ApiOperation(value = "分页跳转", notes = "分页跳转")
	@GetMapping("/list")
	@PermitAll
	@ResponseBody
	public ResultTable list(Tablepar tablepar){
		Page<GoviewProject> page= new Page<GoviewProject>(tablepar.getPage(), tablepar.getLimit());
        Page<GoviewProject> iPages = iGoviewProjectService.page(page, new QueryWrapper());
        ResultTable resultTable=new ResultTable();
		resultTable.setData(iPages.getRecords());
		resultTable.setCode(200);
		resultTable.setCount(iPages.getTotalPage());
		resultTable.setMsg("获取成功");
		return resultTable;
	}


	/**
     * 新增保存
     * @param
     * @return
     */
	//@Log(title = "项目表新增", action = "111")
	@ApiOperation(value = "新增", notes = "新增")
	@PostMapping("/create")
	@ResponseBody
    @PermitAll
	public AjaxResult add(@RequestBody GoviewProject goviewProject){
		goviewProject.setCreateTime(DateUtil.now());
		goviewProject.setState(-1);
		boolean b=iGoviewProjectService.save(goviewProject);
		if(b){
			return successData(200, goviewProject).put("msg", "创建成功");
		}else{
			return error();
		}
	}



	/**
	 * 项目表删除
	 * @param ids
	 * @return
	 */
	//@Log(title = "项目表删除", action = "111")
	@ApiOperation(value = "删除", notes = "删除")
	@DeleteMapping("/delete")
	@ResponseBody
	public AjaxResult remove(String ids){
		List<String> lista= ConvertUtil.toListStrArray(ids);
		Boolean b=iGoviewProjectService.removeByIds(lista);
		if(b){
			return success();
		}else{
			return error();
		}
	}

	@ApiOperation(value = "修改保存", notes = "修改保存")
    @PostMapping("/edit")
    @ResponseBody
    public AjaxResult editSave(@RequestBody GoviewProject goviewProject)
    {
		Boolean b= iGoviewProjectService.updateById(goviewProject);
        if(b){
        	return success();
        }
        return error();
    }


	@ApiOperation(value = "项目重命名", notes = "项目重命名")
    @PostMapping("/rename")
    @ResponseBody
    public AjaxResult rename(@RequestBody GoviewProject goviewProject)
    {

		Boolean b=iGoviewProjectService.updateById(goviewProject);
		if(b){
        	return success();
        }
		return error();
    }


	//发布/取消项目状态
    @PutMapping("/publish")
	@ResponseBody
    public AjaxResult updateVisible(@RequestBody GoviewProject goviewProject){
    	if(goviewProject.getState()==-1||goviewProject.getState()==1) {

    		Boolean b=iGoviewProjectService.updateById(goviewProject);
    		if(b){
            	return success();
            }
    		return error();
    	}
    	return error("警告非法字段");
	}

    @ApiOperation(value = "获取项目存储数据", notes = "获取项目存储数据")
	@GetMapping("/getScreenDSLData")
	@ResponseBody
    public AjaxResult getScreenDSLData(String projectId, ModelMap map)
    {
		GoviewProject goviewProject= iGoviewProjectService.getById(projectId);

		GoviewProjectData blogText=iGoviewProjectDataService.getProjectid(projectId);
		if(blogText!=null) {
			GoviewProjectVo goviewProjectVo=new GoviewProjectVo();
			BeanUtils.copyProperties(goviewProject,goviewProjectVo);
			goviewProjectVo.setContent(blogText.getContent());
			return AjaxResult.successData(200,goviewProjectVo).put("msg","获取成功");
		}
		return AjaxResult.successData(200, null).put("msg","无数据");

    }

	@ApiOperation(value = "保存项目数据", notes = "保存项目数据")
	@PostMapping("/save/data")
	@ResponseBody
	public AjaxResult saveData(GoviewProjectData data) {

		GoviewProject goviewProject= iGoviewProjectService.getById(data.getProjectId());
		if(goviewProject==null) {
			return error("没有该项目ID");
		}
		GoviewProjectData goviewProjectData= iGoviewProjectDataService.getOne(new QueryWrapper().eq(GoviewProjectData::getProjectId, goviewProject.getId()));
		if(goviewProjectData!=null) {
			 data.setId(goviewProjectData.getId());
			 iGoviewProjectDataService.updateById(data);
			 return success("数据保存成功");
		}else {
			iGoviewProjectDataService.save(data);
			return success("数据保存成功");
		}
	}

	/**
	 * 上传文件
	 * @param object 文件流对象
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	public AjaxResult upload(@RequestBody MultipartFile object) throws IOException{
		String fileName = object.getOriginalFilename();
		//默认文件格式
		String suffixName=v2Config.getDefaultFormat();
		String mediaKey="";
		Long filesize= object.getSize();
		//文件名字
		String fileSuffixName="";
		if(fileName.lastIndexOf(".")!=-1) {//有后缀
			 suffixName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
			 //mediaKey=MD5.create().digestHex(fileName);
			 mediaKey=SnowflakeIdWorker.getUUID();
			 fileSuffixName=mediaKey+suffixName;
		}else {//无后缀
			//取得唯一id
			 //mediaKey = MD5.create().digestHex(fileName+suffixName);
			mediaKey= SnowflakeIdWorker.getUUID();
			//fileSuffixName=mediaKey+suffixName;
		}
		String virtualKey=FileController.getFirstNotNull(v2Config.getXnljmap());
		String absolutePath=v2Config.getXnljmap().get(FileController.getFirstNotNull(v2Config.getXnljmap()));
		SysFile sysFile=new SysFile();
		sysFile.setId(SnowflakeIdWorker.getUUID());
		sysFile.setFileName(fileSuffixName);
		sysFile.setFileSize(Integer.parseInt(filesize+""));
		sysFile.setFileSuffix(suffixName);
		sysFile.setCreateTime(DateUtil.formatLocalDateTime(LocalDateTime.now()));
		String filepath=DateUtil.formatDate(new Date());
		sysFile.setRelativePath(filepath);
		sysFile.setVirtualKey(virtualKey);
		sysFile.setAbsolutePath(absolutePath.replace("file:",""));
		iSysFileService.saveOrUpdate(sysFile);
		File desc = FileController.getAbsoluteFile(v2Config.getFileurl()+File.separator+filepath,fileSuffixName);
		object.transferTo(desc);
		SysFileVo sysFileVo=BeanUtil.copyProperties(sysFile, SysFileVo.class);
		sysFileVo.setFileurl(v2Config.getHttpurl()+sysFile.getVirtualKey()+"/"+sysFile.getRelativePath()+"/"+sysFile.getFileName());
		return successData(200, sysFileVo);
	}


}
