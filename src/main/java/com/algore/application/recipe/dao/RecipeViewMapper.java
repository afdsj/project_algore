package com.algore.application.recipe.dao;

import com.algore.application.recipe.dto.*;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface RecipeViewMapper {
    // RecipeviewDTO DetailView

    /* recipeNum에 해당하는 레시피 상세 정보를 조회해서 RecipeviewDTO에 반환*/
    RecipeviewDTO DetailView(int recipeNum);
    /* recipeNum에 해당하는 레시피 순서 정보를 조회해서 List<RecipeOrderDTO>에 반환*/
    List<RecipeOrderDTO> recipeOrder(int recipeNum);
    /* recipeNum에 해당하는 레시피 사진 정보를 조회해서 List<RecipePhotoDTO>에 반환*/
    List<RecipePhotoDTO> recipPhoto(int recipeNum);
    /* recipeNum에 해당하는 레시피 재료 정보를 조회해서 List<RecipeIngredientDTO>에 반환*/
    List<RecipeIngredientDTO> recipeIngredient(int recipeNum);
    /* 모든 재료 정보를 조회해서 List<IngredientDTO>에 반환*/
    List<IngredientDTO> ingredienList();

    /* 레시피 번호에 해당하는 댓글 정보 조회해서 List<CommentReadDTO>형태로 반환 */
    List<CommentReadDTO> commentRead(int recipeNum);

    // ModelAndView modifyForm (/modify)
    /* 레시피 카테고리 정보를 담기 위한 RecipeCategoryDTO 리스트 반환*/
    List<RecipeCategoryDTO> readCategory();
    /* 레시피 번호를 사용해서 해당 레시피 작성자의 이름을 가져오기*/
    String getUserName(int recipeNum);




    int viewCount(int recipeNum);

    int recipeDelete(int recipe);

    int modifyRecipe(RecipeviewDTO recipeviewDTO);

    int orderDelete(int recipeNum);

    int modifyOrder(List<ModifyRecipeOrder> modifyRecipeOrder);

    int modifyPhoto(List<RecipePhotoDTO> recipePhotoDTOList);

    int photoDelete(int recipeNum);

    int writeRecipe(RecipeWriteDTO recipeWriteDTO);




    List<RecipeUnitDTO> readUnit();

    int recipeProcedure(RecipeProcedureDTO recipeProcedureDTO);




    int recipeIngDelete(int recipeNum);

    int modifyIng(List<RecipeIngredientDTO> ingredientDTOList);

    int writeRecipeProduce(List<RecipeProcedureDTO> recipeProcedureDTOList);

    int writeRecipePhotos(List<RecipePhotoWriteDTO> recipePhotoWriteDTOList);

    List<IngredientDTO> readIng();

    int writeRecipeIngredients(List<RecipeIngredientDTO> recipeIngredientDTOList);
}
