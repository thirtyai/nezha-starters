/*
 * Copyright (c) kyle ju(Email: nezha@thirtyai.com  QQ: 17062743) All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.thirtyai.nezha.core.web.file;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.lang.Assert;
import com.thirtyai.nezha.core.web.props.NezhaWebProperties;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

/**
 * 上传文件封装
 *
 * @author kyleju
 */
@Data
public class UploadFile {

	private NezhaWebProperties.UploadFolderConfig folderConfig;

	/**
	 * 上传文件在附件表中的id
	 */
	private Object fileId;

	/**
	 * 上传文件
	 */
	private MultipartFile file;

	/**
	 * 上传分类文件夹
	 */
	private String dir;

	/**
	 * 上传物理路径
	 */
	private String uploadPath;

	/**
	 * 上传虚拟路径
	 */
	private String uploadVirtualPath;

	/**
	 * 文件名
	 */
	private String fileName;

	/**
	 * 真实文件名
	 */
	private String originalFileName;

	public UploadFile(MultipartFile file, NezhaWebProperties.UploadFolderConfig uploadFolderConfig) {
		Assert.notNull(file, "file is null.");
		Assert.notNull(uploadFolderConfig, "upload folder config is null.");
		this.folderConfig = uploadFolderConfig;
		this.dir = dir;
		this.file = file;
		this.fileName = file.getName();
		this.originalFileName = file.getOriginalFilename();
		this.uploadPath = UploadFileUtil.formatUrl(File.separator + uploadFolderConfig.getRealFolder() + File.separator + dir + File.separator + DateUtil.format(DateUtil.date(), "yyyyMMdd") + File.separator + this.originalFileName);
		this.uploadVirtualPath = UploadFileUtil.formatUrl(folderConfig.getContentFolder().replace(uploadFolderConfig.getContentFolder(), "") + File.separator + dir + File.separator + DateUtil.format(DateUtil.date(), "yyyyMMdd") + File.separator + this.originalFileName);
	}

	public UploadFile(MultipartFile file, NezhaWebProperties.UploadFolderConfig uploadFolderConfig, String uploadPath, String uploadVirtualPath) {
		this(file, uploadFolderConfig);
		if (null != uploadPath) {
			this.uploadPath = UploadFileUtil.formatUrl(uploadPath);
			this.uploadVirtualPath = UploadFileUtil.formatUrl(uploadVirtualPath);
		}
	}

	/**
	 * 图片上传
	 */
	public void transfer() {
		transfer(folderConfig.isCompress());
	}

	/**
	 * 图片上传
	 *
	 * @param compress 是否压缩
	 */
	public void transfer(boolean compress) {
		this.transfer(new LocalFileUploadProxy(), compress);
	}

	/**
	 * 图片上传
	 *
	 * @param uploadProxy 文件上传工厂类
	 * @param compress    是否压缩
	 */
	public void transfer(IUploadProxy uploadProxy, boolean compress) {
		try {
			File file = new File(uploadPath);

			if (null != uploadProxy) {
				String[] path = uploadProxy.path(file, dir);
				this.uploadPath = path[0];
				this.uploadVirtualPath = path[1];
				file = uploadProxy.rename(file, path[0]);
			}

			File pfile = file.getParentFile();
			if (!pfile.exists()) {
				pfile.mkdirs();
			}

			this.file.transferTo(file);

			if (compress) {
				uploadProxy.compress(this.uploadPath);
			}

		} catch (IllegalStateException | IOException e) {
			e.printStackTrace();
		}
	}
}
