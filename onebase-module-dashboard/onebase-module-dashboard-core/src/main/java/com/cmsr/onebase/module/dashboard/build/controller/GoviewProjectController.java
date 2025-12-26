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
import com.cmsr.onebase.module.dashboard.build.model.DashboardProject;
import com.cmsr.onebase.module.dashboard.build.model.DashboardProjectData;
import com.cmsr.onebase.module.dashboard.build.model.DashboardFile;
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
	public CommonResult<PageResult<DashboardProject>> list(Tablepar tablepar){
		Page<DashboardProject> page= new Page<>(tablepar.getPage(), tablepar.getLimit());
        Page<DashboardProject> iPages = iGoviewProjectService.page(page, new QueryWrapper().eq(DashboardProject::getAppId,
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
	public AjaxResult add(@RequestBody DashboardProject dashboardProject){
		dashboardProject.setState(-1);
		boolean b=iGoviewProjectService.save(dashboardProject);
		if(b){
			return successData(0, dashboardProject.getId()).put("msg", "交易成功");
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
    public AjaxResult editSave(@RequestBody DashboardProject dashboardProject)
    {
		Boolean b= iGoviewProjectService.updateById(dashboardProject);
        if(b){
        	return successData(0, dashboardProject).put("msg", "交易成功");
        }
        return error();
    }


	@ApiOperation(value = "项目重命名", notes = "项目重命名")
    @PostMapping("/rename")
    @ResponseBody
	@ApiSignIgnore
    public AjaxResult rename(@RequestBody DashboardProject dashboardProject)
    {

		Boolean b=iGoviewProjectService.updateById(dashboardProject);
		if(b){
        	return successData(0, dashboardProject.getId()).put("msg", "交易成功");
        }
		return error();
    }


	//发布/取消项目状态
    @PostMapping("/publish")
	@ResponseBody
	@PermitAll
	@ApiSignIgnore
	@TenantIgnore
    public AjaxResult updateVisible(@RequestBody DashboardProject dashboardProject){
    	if(dashboardProject.getState()==-1|| dashboardProject.getState()==1) {

    		Boolean b=iGoviewProjectService.updateById(dashboardProject);
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
		DashboardProject dashboardProject = iGoviewProjectService.getById(projectId);

		DashboardProjectData blogText=iGoviewProjectDataService.getProjectid(projectId);
		if(blogText!=null) {
			GoviewProjectVo goviewProjectVo=new GoviewProjectVo();
			BeanUtils.copyProperties(dashboardProject,goviewProjectVo);
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
	public AjaxResult saveData(DashboardProjectData data) {

		DashboardProject dashboardProject = iGoviewProjectService.getById(data.getProjectId());
		if(dashboardProject ==null) {
			return error("没有该项目ID");
		}
		DashboardProjectData dashboardProjectData = iGoviewProjectDataService.getOne(new QueryWrapper().eq(DashboardProjectData::getProjectId, dashboardProject.getId()));
		if(dashboardProjectData !=null) {
			 data.setId(dashboardProjectData.getId());
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

		DashboardFile dashboardFile =new DashboardFile();
		dashboardFile.setId(SnowflakeIdWorker.getUUID());
		dashboardFile.setFileName(fileSuffixName);
		dashboardFile.setFileSize(Integer.parseInt(filesize+""));
		dashboardFile.setFileSuffix(suffixName);
		dashboardFile.setCreateTime(LocalDateTime.now());
		String filepath= DateUtil.formatDate(new Date());
		dashboardFile.setRelativePath(filepath);
		dashboardFile.setVirtualKey(virtualKey);
		dashboardFile.setAbsolutePath(absolutePath.replace("file:",""));

		iSysFileService.saveOrUpdate(dashboardFile);

		return fileApi.dashboardUpload(new FileCreateReqDTO().setName(object.getOriginalFilename())
				.setType(object.getContentType()).setContent(object.getBytes()));
	}


	@GetMapping("/download/{id}")
	@Operation(summary = "获取文件内容")
	@PermitAll
	@TenantIgnore
	@ApiSignIgnore
	@Parameter(name = "id", description = "文件编号", required = true)
	public void getFileContent(@PathVariable("id") Long id, HttpServletRequest request, HttpServletResponse response) throws Exception {
		fileApi.dashboardDownload(id, request, response);
	}

}
