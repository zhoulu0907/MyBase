package com.cmsr.onebase.module.dashboard.build.controller;

import cn.hutool.core.date.DateUtil;
import com.cmsr.onebase.framework.common.annotaion.ApiSignIgnore;
import com.cmsr.onebase.framework.common.pojo.CommonResult;
import com.cmsr.onebase.framework.common.pojo.PageResult;
import com.cmsr.onebase.framework.tenant.core.aop.TenantIgnore;
import com.cmsr.onebase.module.dashboard.build.common.base.BaseController;
import com.cmsr.onebase.module.dashboard.build.common.config.V2Config;
import com.cmsr.onebase.module.dashboard.build.common.domain.AjaxResult;
import com.cmsr.onebase.module.dashboard.build.common.domain.Tablepar;
import com.cmsr.onebase.module.dashboard.build.model.GoviewProject;
import com.cmsr.onebase.module.dashboard.build.model.GoviewProjectData;
import com.cmsr.onebase.module.dashboard.build.model.SysFile;
import com.cmsr.onebase.module.dashboard.build.model.vo.GoviewProjectVo;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectDataService;
import com.cmsr.onebase.module.dashboard.build.service.IGoviewProjectService;
import com.cmsr.onebase.module.dashboard.build.service.ISysFileService;
import com.cmsr.onebase.module.dashboard.build.util.SnowflakeIdWorker;
import com.cmsr.onebase.module.infra.api.file.FileApi;
import com.cmsr.onebase.module.infra.api.file.dto.FileCreateReqDTO;
import com.mybatisflex.core.paginate.Page;
import com.mybatisflex.core.query.QueryWrapper;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.annotation.Resource;
import jakarta.annotation.security.PermitAll;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Date;

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
	@Resource
	private FileApi fileApi;
	@ApiOperation(value = "分页跳转", notes = "分页跳转")
	@GetMapping("/list")
	@ResponseBody
	@ApiSignIgnore
	public CommonResult<PageResult<GoviewProject>> list(Tablepar tablepar){
		Page<GoviewProject> page= new Page<>(tablepar.getPage(), tablepar.getLimit());
        Page<GoviewProject> iPages = iGoviewProjectService.page(page, new QueryWrapper().eq(GoviewProject::getAppId,
				tablepar.getAppId(),tablepar.getAppId() != null));

		return CommonResult.success(new PageResult<>(iPages.getRecords(), (long) iPages.getRecords().size()));
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
	@ApiSignIgnore
	public AjaxResult add(@RequestBody GoviewProject goviewProject){
		goviewProject.setState(-1);
		boolean b=iGoviewProjectService.save(goviewProject);
		if(b){
			return successData(0, goviewProject.getId()).put("msg", "交易成功");
		}else{
			return error();
		}
	}



	/**
	 * 项目表删除
	 * @param
	 * @return
	 */
	//@Log(title = "项目表删除", action = "111")
	@ApiOperation(value = "删除", notes = "删除")
	@PostMapping("/delete")
	@ResponseBody
	@ApiSignIgnore
	public AjaxResult remove(@RequestParam Long id){
		Boolean b=iGoviewProjectService.removeById(id);
		if(b){
			return successData(0,id).put("msg", "交易成功");
		}else{
			return error();
		}
	}

	@ApiOperation(value = "修改保存", notes = "修改保存")
    @PostMapping("/edit")
    @ResponseBody
	@PermitAll
	@ApiSignIgnore
	@TenantIgnore
    public AjaxResult editSave(@RequestBody GoviewProject goviewProject)
    {
		Boolean b= iGoviewProjectService.updateById(goviewProject);
        if(b){
        	return successData(0,goviewProject).put("msg", "交易成功");
        }
        return error();
    }


	@ApiOperation(value = "项目重命名", notes = "项目重命名")
    @PostMapping("/rename")
    @ResponseBody
	@ApiSignIgnore
    public AjaxResult rename(@RequestBody GoviewProject goviewProject)
    {

		Boolean b=iGoviewProjectService.updateById(goviewProject);
		if(b){
        	return successData(0,goviewProject.getId()).put("msg", "交易成功");
        }
		return error();
    }


	//发布/取消项目状态
    @PostMapping("/publish")
	@ResponseBody
	@PermitAll
	@ApiSignIgnore
	@TenantIgnore
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
	@PermitAll
	@ApiSignIgnore
	@TenantIgnore
    public CommonResult<GoviewProjectVo>  getScreenDSLData(Long projectId, ModelMap map)
    {
		GoviewProject goviewProject= iGoviewProjectService.getById(projectId);

		GoviewProjectData blogText=iGoviewProjectDataService.getProjectid(projectId);
		if(blogText!=null) {
			GoviewProjectVo goviewProjectVo=new GoviewProjectVo();
			BeanUtils.copyProperties(goviewProject,goviewProjectVo);
			goviewProjectVo.setId(String.valueOf(goviewProject.getId()));
			goviewProjectVo.setContent(blogText.getContent());
			return CommonResult.success(goviewProjectVo);
		}
		return CommonResult.success(null);

    }

	@ApiOperation(value = "保存项目数据", notes = "保存项目数据")
	@PostMapping("/save/data")
	@ResponseBody
	@ApiSignIgnore
	@PermitAll
	@TenantIgnore
	public AjaxResult saveData(GoviewProjectData data) {

		GoviewProject goviewProject= iGoviewProjectService.getById(data.getProjectId());
		if(goviewProject==null) {
			return error("没有该项目ID");
		}
		GoviewProjectData goviewProjectData= iGoviewProjectDataService.getOne(new QueryWrapper().eq(GoviewProjectData::getProjectId, goviewProject.getId()));
		if(goviewProjectData!=null) {
			 data.setId(goviewProjectData.getId());
			 iGoviewProjectDataService.updateById(data);
			return successData(0,data.getProjectId()).put("msg", "数据保存成功");
		}else {
			iGoviewProjectDataService.save(data);
			return successData(0,data.getProjectId()).put("msg", "数据保存成功");
		}
	}

	/**
	 * 上传文件
	 * @param object 文件流对象
	 * @return
	 * @throws Exception
	 */
	// @PostMapping("/upload")
	// @PermitAll
	// @ApiSignIgnore
	// @TenantIgnore
	// public AjaxResult upload(MultipartFile object) throws IOException{
	// 	String fileName = object.getOriginalFilename();
	// 	//默认文件格式
	// 	String suffixName=v2Config.getDefaultFormat();
	// 	String mediaKey="";
	// 	Long filesize= object.getSize();
	// 	//文件名字
	// 	String fileSuffixName="";
	// 	if(fileName.lastIndexOf(".")!=-1) {//有后缀
	// 		 suffixName = fileName.substring(fileName.lastIndexOf(".")).toLowerCase();
	// 		 //mediaKey=MD5.create().digestHex(fileName);
	// 		 mediaKey=SnowflakeIdWorker.getUUID();
	// 		 fileSuffixName=mediaKey+suffixName;
	// 	}else {//无后缀
	// 		//取得唯一id
	// 		 //mediaKey = MD5.create().digestHex(fileName+suffixName);
	// 		mediaKey= SnowflakeIdWorker.getUUID();
	// 		//fileSuffixName=mediaKey+suffixName;
	// 	}
	// 	String virtualKey=FileController.getFirstNotNull(v2Config.getXnljmap());
	// 	String absolutePath=v2Config.getXnljmap().get(FileController.getFirstNotNull(v2Config.getXnljmap()));
	// 	SysFile sysFile=new SysFile();
	// 	sysFile.setId(SnowflakeIdWorker.getUUID());
	// 	sysFile.setFileName(fileSuffixName);
	// 	sysFile.setFileSize(Integer.parseInt(filesize+""));
	// 	sysFile.setFileSuffix(suffixName);
	// 	sysFile.setCreateTime(LocalDateTime.now());
	// 	String filepath=DateUtil.formatDate(new Date());
	// 	sysFile.setRelativePath(filepath);
	// 	sysFile.setVirtualKey(virtualKey);
	// 	sysFile.setAbsolutePath(absolutePath.replace("file:",""));
	// 	iSysFileService.saveOrUpdate(sysFile);
	// 	File uploadDir = new File(v2Config.getFileurl() + File.separator + filepath);
	// 	if (!uploadDir.exists()) {
	// 		boolean dirCreated = uploadDir.mkdirs();
	// 		if (!dirCreated) {
	// 			throw new IOException("无法创建上传目录: " + uploadDir.getAbsolutePath());
	// 		}
	// 	}
	// 	File desc = new File(uploadDir, fileSuffixName);
	// 	// 确保目标文件存在后再进行传输
	// 	if (!desc.exists()) {
	// 		desc.createNewFile();
	// 	}
	// 	object.transferTo(desc);
	// 	SysFileVo sysFileVo=BeanUtil.copyProperties(sysFile, SysFileVo.class);
	// 	sysFileVo.setFileurl(v2Config.getHttpurl()+sysFile.getVirtualKey()+"/"+sysFile.getRelativePath()+"/"+sysFile.getFileName());
	// 	return successData(0, sysFileVo);
	// }


	/**
	 * 上传文件
	 * @param object 文件流对象
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/upload")
	@PermitAll
	@ApiSignIgnore
	@TenantIgnore
	public CommonResult<String> upload(MultipartFile object) throws IOException{

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
		sysFile.setCreateTime(LocalDateTime.now());
		String filepath= DateUtil.formatDate(new Date());
		sysFile.setRelativePath(filepath);
		sysFile.setVirtualKey(virtualKey);
		sysFile.setAbsolutePath(absolutePath.replace("file:",""));

		CommonResult<String> result = fileApi.dashboardUpload(new FileCreateReqDTO().setName(object.getOriginalFilename())
				.setType(object.getContentType()).setContent(object.getBytes()));
		// 保存文件标识
		sysFile.setFileId(Long.valueOf(result.getData()));

		iSysFileService.saveOrUpdate(sysFile);

		return result;
	}


}
