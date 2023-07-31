package com.algore.application.recipe.service;

import com.algore.application.recipe.dao.RecipeViewMapper;
import com.algore.application.recipe.dto.*;
import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class RecipeService {
    /* RecipeViewMapper는 변하지 않아야 하기 때문에 final 사용! (초기화 의무화 시키기)*/
    public final RecipeViewMapper mapper;

    public RecipeService(RecipeViewMapper mapper) {
        /*final은 기본값이 없기 때문에 초기화를 통해 값을 등록해 주어야함*/
        this.mapper = mapper;
    }

    /* 레시피 상세 정보를 조회하는 메서드 (recipeDetailView)*/
    public RecipeviewDTO DetailView(int recipeNum) {
        /* mapper에 있는 DetailView 메서드를 호출해서
         * recipeNum에 해당하는 레시피 상세 정보를 조회해서 recipeviewDTO에 결과 담기*/
        RecipeviewDTO recipeviewDTO = mapper.DetailView(recipeNum);
        /* mapper에 있는 recipeOrder 메서드를 호출해서
        *  recipeNum에 해당하는 레시피 순서 정보를 조회해서 recipeOrderDTOList에 리스트 담기*/
        List<RecipeOrderDTO> recipeOrderDTOList = mapper.recipeOrder(recipeNum);
        /* mapper에 있는 recipPhoto 메서드를 호출해서
        *  recipeNum에 해당하는 레시피 사진 정보를 조회해서 recipePhotoDTOList에 리스트 담기*/
        List<RecipePhotoDTO> recipePhotoDTOList = mapper.recipPhoto(recipeNum);
        /* mapper에 있는 recipeIngredient 메서드를 호출해서
        *  recipeNum에 해당하는 레시피 재료 정보를 조회해서 recipeIngredientDTOS에 리스트 담기*/
        List<RecipeIngredientDTO> recipeIngredientDTOS = mapper.recipeIngredient(recipeNum);
        /* mapper에 있는 ingredienList 메서드를 호출해서
         * 모든 재료 정보를 조회해서 ingredientDTOList에 리스트 담기*/
        List<IngredientDTO> ingredientDTOList = mapper.ingredienList();
        /* 모든 재료의 가격을 합산하기 위해서 allPrice를 0으로 초기화시킴*/
        int allPrice =0;
        /* recipeIngredientDTOS 리스트에 있는 RecipeIngredientDTO 객체를 반복하기 위한 반복문 */
        for (RecipeIngredientDTO recipeIngredientDTO:recipeIngredientDTOS) {
            /* recipeIngredientDTO에 있는 getPrice 메소드를 호출해서
            *  재료의 가격을 합친 후에 allPrice에 담아준다 */
            allPrice += recipeIngredientDTO.getPrice();

        }
        System.out.println();
        /* 레시피의 모든 재료 정보 담기*/
        recipeviewDTO.setIngredientDTOList(ingredientDTOList);
        /* 레시피의 전체 가격 담기*/
        recipeviewDTO.setAllPrice(allPrice);
        /* 레시피의 재료 정보 담기*/
        recipeviewDTO.setRecipeIngredientDTOS(recipeIngredientDTOS);
        /* 레시피의 순서 정보 담기*/
        recipeviewDTO.setRecipeOrderList(recipeOrderDTOList);
        /* 레시피의 사진 정보 담기*/
        recipeviewDTO.setRecipePhotoDTOList(recipePhotoDTOList);
        /* recipeviewDTO 반환해서 결과 보여주기*/
        return recipeviewDTO;
    }

    /* 댓글 정보를 조회하는 메서드*/
    public List<CommentReadDTO> commentRead(int recipeNum) {
        /* mapper에 있는 commentRead 메서드 호출해서
        *  recipeNum에 해당하는 레시피 댓글 정보 조회해서 commentReadDTOList에 담기*/
        List<CommentReadDTO> commentReadDTOList = mapper.commentRead(recipeNum);
        /* commentReadDTOList 반환해서 결과 보여주기*/
        return commentReadDTOList;
    }

    /* 레시피 카테고리 목록을 가져오는 메서드 (/modify)*/

     public List<RecipeCategoryDTO> readcategory() {
         /* mapper에 있는 readCategory 메서드 호출해서
         *  recipecategory에 담기*/
         List<RecipeCategoryDTO> recipecategory = mapper.readCategory();
         /* recipecategory 반환해서 레시피 카테고리 목록 전달*/
         return recipecategory;
    }

    /* 레시피 작성자의 이름을 가져오는 메서드 (/modify)*/
    public String getUserName(int recipeNum) {
        /* mapper에 있는 getUserName 메서드 호출해서
        *  recipeNum에 해당하는 레시피 작성자의 이름을 가져와서 name에 담기*/
        String name = mapper.getUserName(recipeNum);
        /* name에 저장된 레시피 작성자의 이름 반환*/
        return name;
    }


    public int viewCount(int recipeNum) {
        int result = mapper.viewCount(recipeNum);
        return result;
    }



    public int recipeDelete(int recipe) {
        int result = mapper.recipeDelete(recipe);
        return result;
    }


    public int writeRecipe(RecipeWriteDTO recipeWriteDTO) {

        int result = mapper.writeRecipe(recipeWriteDTO);

        if(result > 0){

            List<RecipeProcedureDTO> recipeProcedureDTOList = recipeWriteDTO.getRecipeProcedureDTOList();

            if(recipeProcedureDTOList != null && !recipeProcedureDTOList.isEmpty()){
                for(RecipeProcedureDTO recipeProcedure : recipeProcedureDTOList){
                    recipeProcedure.setRpNum(result);
                }
                mapper.writeRecipeProduce(recipeProcedureDTOList);
            }
            List<RecipePhotoWriteDTO> recipePhotoWriteDTOList = recipeWriteDTO.getRecipePhotoWriteDTOList();
            if (recipePhotoWriteDTOList != null && !recipePhotoWriteDTOList.isEmpty()) {
                for (RecipePhotoWriteDTO recipePhotoWriteDTO : recipePhotoWriteDTOList) {
                    recipePhotoWriteDTO.setRecipeNum(result);
                }
                mapper.writeRecipePhotos(recipePhotoWriteDTOList);
            }

            List<RecipeIngredientDTO> recipeIngredientDTOList = recipeWriteDTO.getRecipeIngredientDTOList();
            if (recipeIngredientDTOList != null && !recipeIngredientDTOList.isEmpty()) {
                for (RecipeIngredientDTO recipeIngredientDTO : recipeIngredientDTOList) {
                    recipeIngredientDTO.setRecipeNum(result);
                }
                mapper.writeRecipeIngredients(recipeIngredientDTOList);
            }

        }
        return result;
    }

    public int modifyRecipe(RecipeviewDTO recipeviewDTO) {
        System.out.println("여기");
        int result = 0;

        int IngDelete =  mapper.recipeIngDelete(recipeviewDTO.getRecipeNum());
        int orderDelete = mapper.orderDelete(recipeviewDTO.getRecipeNum());

        int photoDelete = mapper.photoDelete(recipeviewDTO.getRecipeNum());
        System.out.println("여기");
        int orderResult = mapper.modifyOrder(recipeviewDTO.getModifyRecipeOrders());
        System.out.println("여기");
        int IngResult = mapper.modifyIng(recipeviewDTO.getRecipeIngredientDTOS());
        System.out.println("여기");

        int recipeResult = mapper.modifyRecipe(recipeviewDTO);
        int photoResult = mapper.modifyPhoto(recipeviewDTO.getRecipePhotoDTOList());

        if (recipeResult > 0) {
            System.out.println("성공");
            result = 1;
        }


        return result;
    }



    public List<RecipeUnitDTO> readUnit() {

        List<RecipeUnitDTO> recipeunit = mapper.readUnit();
        System.out.println(recipeunit);
        return recipeunit;
    }

    public List<IngredientDTO> readIng() {

        List<IngredientDTO> recipeing = mapper.readIng();
        System.out.println(recipeing);
        return recipeing;
    }
}

