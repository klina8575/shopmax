package com.shopmax.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import com.shopmax.entity.ItemImg;
import com.shopmax.repository.ItemImgRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.thymeleaf.util.StringUtils;


@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {
	
	private String itemImgLocation = "C:/shop/item";
	private final ItemImgRepository itemImgRepository;
	private final FileService fileService;
	
	/*
	 * 이미지 저장
	    1.파일을 itemImgLocation에 저장
	    2.item_img 테이블에 저장
	*/
	public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {
		String oriImgName = itemImgFile.getOriginalFilename(); //파일이름 -> 이미지1.jpg
		String imgName = "";
		String imgUrl = "";

		//1.파일을 itemImgLocation에 저장
		if(!StringUtils.isEmpty(oriImgName)) {
			//oriImgName이 빈문자열이 아니라면 이미지 파일 업로드
			imgName = fileService.uploadFile(itemImgLocation, 
					oriImgName, itemImgFile.getBytes());
			imgUrl = "/images/item/" + imgName;
			
		}
		
		//2.item_img 테이블에 저장
		
		//(이미지1.jpg, ERSFHG4FDGD454.jpg, /images/item/ERSFHG4FDGD454.jpg)로 entity값을 update
		itemImg.updateItemImg(oriImgName, imgName, imgUrl);
		itemImgRepository.save(itemImg); //db에 insert
	}
	
	
}















