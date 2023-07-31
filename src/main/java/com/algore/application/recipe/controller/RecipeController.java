package com.algore.application.recipe.controller;

import com.algore.application.recipe.dto.*;


import com.algore.application.recipe.service.RecipeService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Controller
@RequestMapping("/recipe")

public class RecipeController {
    /* RecipeService는 변하지 않아야 하기 때문에 final 사용! (초기화 의무화 시키기)*/
    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        /*final은 기본값이 없기 때문에 초기화를 통해 값을 등록해 주어야함*/
        this.recipeService = recipeService;
    }

    /* 레시피 상세 정보를 보여주는 페이지*/
    @GetMapping("/view")
    public ModelAndView recipeDetailView(ModelAndView mv, int recipe, Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
        /* 조회수 증가*/
        viewCount(request, response, recipe);
        /* recipeService에서 DetailView 메서드를 호출해서 recipe에 해당하는
         *  레시피 정보를 recipeviewDTO에 생성*/
        RecipeviewDTO recipeviewDTO = recipeService.DetailView(recipe);
        /* recipeSerivce에서 commentRead 메서드를 호출해서 recipe에 해당하는
         *  댓글 목록을 commentReadDTOList에 생성*/
        List<CommentReadDTO> commentReadDTOList = recipeService.commentRead(recipe);
        /* mv 객체에 "commentRead"라는 이름으로 commentReadDTOList 추가하기*/
        mv.addObject("commentRead", commentReadDTOList);
        /* mv 객체에 "recipevlew"라는 이름으로 recipeviewDTO 추가하기*/
        mv.addObject("recipevlew", recipeviewDTO);
        /* mv 객체에 뷰이름을 "/recipe/view"로 경로 설정*/
        mv.setViewName("/recipe/view");
        /* mv 객체를 반환해서 결과 보여주기*/
        return mv;
    }

    /* @ResponseBody : 요청된 데이터를 처리 후 서버에서 클라이언트로 다시 응답 데이터를 보낼때
    *                  Java Object에서 JSON 또는 XML 같은 형식으로 변환이 필요한데
    *                  이때 이러한 과정들을 처리해주는 어노테이션!*/
    @ResponseBody
    @GetMapping("/modify")
    public ModelAndView modifyForm(ModelAndView mv, Authentication authentication, @RequestParam("recipe") int recipe) {
        try {
            /* 레시피 카테고리 정보 조회
            *  recipeService에서 readcategory 메서드 호출해서 List<RecipeCategoryDTO> 타입의 recipeCategoryDTO 생성*/
            List<RecipeCategoryDTO> recipeCategoryDTO = recipeService.readcategory();
            /* 레시피 작성자의 이름 조회
            *  recipeService에서 getUserName 메서드 호출해서 name에 생성*/
            String name = recipeService.getUserName(recipe);
            /* 수정된 레시피 순서 정보를 담을 ArrayList 생성*/
            List<RecipeOrderDTO> newOrderDTO = new ArrayList<>();

               /* System.out.println(authentication.getDetails());
                System.out.println(authentication.isAuthenticated());
                System.out.println(authentication.getPrincipal());
                System.out.println(authentication.getAuthorities());*/

            // 인증된 사용자의 이름과 레시피 작성자의 이름이 다른 경우 수정 불가능하도록 처리

            /* 로그인한 사용자의 이름과 레시피 작성자의 이름이 다를 경우 실행하는 메서드*/
            if (!authentication.getName().equals(name)) {
                //작성자만 수정가능
                /* mv 객체에 "message"라는 이름으로 "작성자만 수정 가능합니다"로 추가*/
                mv.addObject("message", "작성자만 수정 가능합니다.");
                /* mv 객체의 뷰 이름을 "/common/error"로 경로 설정 */
                mv.setViewName("/common/error");
                /* 수정 권한이 없는 사용자가 해당 페이지에 접근하면 에러 페이지 발생*/
                return mv;
            }
            /* 레시피 상세 정보를 조회하는 과정에서 레시피의 사진 정보가 없을 경우
            *  기본적으로 4개의 RecipePhotoDTO 객체를 생성해서 RecipeviewDTO 객체에 추가하는 로직*/

            /* recipeService에서 DetailView 메서드를 호출해서
            *  recipe에 해당하는 레시피 상세 정보를 조회해서 recipeviewDTO에 생성*/
            RecipeviewDTO recipeviewDTO = recipeService.DetailView(recipe);
            /* recipePhotoDTO에서 getRecipePhotoDTOList 메서드를 호출해서
            *  List<RecipePhotoDTO>를 가져오기*/
            List<RecipePhotoDTO> recipePhotoDTOList = recipeviewDTO.getRecipePhotoDTOList();
            /* 사진 정보가 없는 경우 확인하기 위해 recipePhotoDTOList가 비어있는지 확인*/
            if (recipePhotoDTOList.isEmpty()) {
                /* 사진 정보가 없을 경우 비어 있는 RecipePhotoDTO를 4개 생성하기 위한 반복문*/
                for (int i = 0; i < 4; i++) {
                    /* 비어 있는 RecipePhotoDTO를 recipePhotoDTOList에 추가*/
                    recipePhotoDTOList.add(new RecipePhotoDTO());
                }
                /* 생성된 RecipePhotoDTO 객체들이 담긴 recipePhotoDTOList를 recipeviewDTO 객체에 설정해준다
                *  recipeviewDTO는 레시피의 사진 정보가 없는 경우에도 4개의 RecipePhotoDTO를 가지게 된다*/
                recipeviewDTO.setRecipePhotoDTOList(recipePhotoDTOList);
            }
            /* mv 객체에 "newOrder"라는 이름으로 newOrderDTO를 추가
            *  -> 뷰에서 ${newOrder}와 같이 해당 데이터를 사용할 수 있다*/
            mv.addObject("newOrder", newOrderDTO);
            /* mv 객체에 "recipeCategory"라는 이름으로 recipeCategoryDTO를 추가
            *  -> 뷰에서 ${recipeCategory}와 같이 해당 데이터 사용할 수 있다*/
            mv.addObject("recipeCategory", recipeCategoryDTO);
            /* mv 객체에 "recipevlew"라는 이름으로 recipeviewDTO를 추가
            *  -> 뷰에서 ${recipevlew}와 같이 해당 데이터를 사용할 수 있다*/
            mv.addObject("recipevlew", recipeviewDTO);
            /* mv 객체의 뷰 이름을 "/recipe/modify"로 경로 설정*/
            mv.setViewName("/recipe/modify");
        } catch (Exception e) {
            /* 예외 발생한 경우 에러메시지 발생*/
            mv.addObject("message", e.getMessage());
            /* mv 객체의 뷰 이름을 "/common/error"로 경로 설정*/
            mv.setViewName("/common/error");
        }
        /* mv 객체를 반환해서 결과를 클라이언트에게 보여준다*/
        return mv;
    }

    @PostMapping("/modifyform")
    @ResponseBody
    public ModelAndView modifyRecipe(ModelAndView mv, RecipeviewDTO recipeviewDTO, HttpServletRequest
            request, @RequestParam(value = "oprderInputFile", required = false) List<MultipartFile> recipePicture,
                                     @RequestParam(value = "orderContent", required = false) List<String> orderContent,
                                     @RequestParam(value = "ingName", required = false) int[] ingName,
                                     @RequestParam(value = "weigh", required = false) String[] weigh,
                                     @RequestParam(value = "riUnitNum", required = false) int[] riUnitNum,
                                     RedirectAttributes rttr) {

        try {
            String root = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\upload\\basic\\";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            List<ModifyRecipeOrder> modifyRecipeOrders = new ArrayList<>();
            List<RecipeOrderDTO> recipeOrderDTOS = recipeviewDTO.getRecipeOrderList();
            List<RecipePhotoDTO> recipePhotoDTOList = recipeviewDTO.getRecipePhotoDTOList();
            int recipeNum = recipeviewDTO.getRecipeNum();

            //대표 사진 로직
            MultipartFile mainFile = recipeviewDTO.getMainInputFile();
            String mainFilename = mainFile.getOriginalFilename();
            //파일 이름이 널이 아닐때 만 실행

            if (!mainFile.isEmpty()) {
                File profileFile = new File(root + recipeviewDTO.getMainFileName());
                System.out.println(profileFile);
                if (profileFile.exists()) {
                    profileFile.delete();
                }
                //이름 중복 해결하기 위해서 시간으로 이름 교체
                String mainfileName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + "." + mainFilename.substring(mainFilename.lastIndexOf(".") + 1);

                mainFile.transferTo(new File(root + "\\" + mainfileName));
                //교체한 이름 DTO에 세팅
                recipeviewDTO.setMainFileName(mainfileName);
                recipeviewDTO.setMainPath("/upload/basic/");
            }

            //요리순서 로직
            for (int i = 0; i < orderContent.size(); i++) {
                if (recipePicture.get(i).getOriginalFilename().equals("")) {    //파일의 이름이 비어있으면
                    if (i < recipeOrderDTOS.size()) {
                        modifyRecipeOrders.add(new ModifyRecipeOrder(recipeNum, orderContent.get(i), recipeOrderDTOS.get(i).getFileName(), recipeOrderDTOS.get(i).getPath()));
                    }
                } else {
                    if (i < recipeOrderDTOS.size()) {
                        File file = new File(root + "\\" + recipeOrderDTOS.get(i).getFileName()); // 이미 저장한 파일이름 가져오기

                        if (file.exists()) { // 파일 있는지 확인
                            file.delete(); // 파일 이 있으면 삭제하기
                        }
                    }
                    // list에 파일 이름 다시 등록해주기
                    // 파일 이름 안겹치게 하기
                    String newPhotoName = recipePicture.get(i).getOriginalFilename();
                    String newOrderFileName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + "." + newPhotoName.substring(newPhotoName.lastIndexOf(".") + 1);

                    recipePicture.get(i).transferTo(new File(root + "\\" + newOrderFileName));
                    modifyRecipeOrders.add(new ModifyRecipeOrder(recipeNum, orderContent.get(i), newOrderFileName, "/upload/basic/"));

                }
            }

            //완성사진
            for (int i = 0; i < recipePhotoDTOList.size(); i++) {
                //레시피 넘버 세팅
                recipePhotoDTOList.get(i).setRecipeNum(recipeviewDTO.getRecipeNum());

                //등록된 파일의 이름을 가져오기
                String photoName = recipePhotoDTOList.get(i).getPhotoInputFile().getOriginalFilename();
                //등록된 파일이 있는지 검사
                if (photoName != null && !photoName.equals("")) {
                    System.out.println(photoName);
                    //등록된 파일이 있으면 기존에 있던 파일 삭제해주고 이름 바꿔주기
                    File file = new File(root + "//" + recipePhotoDTOList.get(i).getRecipeFileName());
                    if (file.exists()) { //파일이 있으면
                        file.delete(); //삭제 해주기
                    }
                    //list에 파일 이름 다시 등록해주기
                    String newPhotoName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + "." + photoName.substring(photoName.lastIndexOf(".") + 1);
                    System.out.println(newPhotoName);
                    recipePhotoDTOList.get(i).getPhotoInputFile().transferTo(new File(root + "\\" + newPhotoName));
                    recipePhotoDTOList.get(i).setRecipePhotoPath(("/upload/basic/"));
                    recipePhotoDTOList.get(i).setRecipeFileName(newPhotoName);
                    System.out.println(newPhotoName);
                }


            }

            //재료 로직

            List<RecipeIngredientDTO> recipeIngredientDTOS = new ArrayList<>();
            for (int i = 0; i < ingName.length; i++) {
                if (!(ingName[i] == 0)) {
                    recipeIngredientDTOS.add(new RecipeIngredientDTO(ingName[i], recipeNum, Integer.parseInt(weigh[i]), riUnitNum[i]));
                    System.out.println(ingName[i] + "이름");
                    System.out.println(weigh[i] + "용량");
                    System.out.println(riUnitNum[i] + "단위");

                }
            }

            for (RecipeIngredientDTO recipeIngredientDTO : recipeIngredientDTOS) {
                System.out.println(recipeIngredientDTO);
            }
            recipeviewDTO.setRecipeIngredientDTOS(recipeIngredientDTOS);
            recipeviewDTO.setModifyRecipeOrders(modifyRecipeOrders);
            recipeviewDTO.setRecipePhotoDTOList(recipePhotoDTOList);
            int result = recipeService.modifyRecipe(recipeviewDTO);


            if (result > 0) {
                rttr.addFlashAttribute("message", "게시글 수정 성공");
                mv.setViewName("redirect:/recipe/view?recipe=" + recipeviewDTO.getRecipeNum());
            } else {
                mv.addObject("message", "수정 실패하였습니다.");
                mv.setViewName("/common/error");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mv;
    }


    @GetMapping("/delete")
    public ModelAndView recipeDelete(ModelAndView mv, Authentication authentication,
                                     @RequestParam("recipe") int recipe) {
        try {


            String name = recipeService.getUserName(recipe);
            if (!authentication.getName().equals(name)) {
                //작성자만 삭제가능
                mv.addObject("message", "작성자만 삭제 가능합니다.");
                mv.setViewName("/home");
                return mv;
            }
            int result = recipeService.recipeDelete(recipe);
            mv.setViewName("redirect:/home/?page=1");
            return mv;
        } catch (Exception e) {
            return mv;
        }
    }


    private void viewCount(HttpServletRequest request, HttpServletResponse response, int recipeNum) {
        Cookie oldCookie = null;

        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("recipeView")) {
                    oldCookie = cookie;
                }
            }
        }

        if (oldCookie != null) {
            if (!oldCookie.getValue().contains("[" + recipeNum + "]")) {
                recipeService.viewCount(recipeNum);
                oldCookie.setValue(oldCookie.getValue() + "_[" + recipeNum + "]");
                oldCookie.setPath("/");
                oldCookie.setMaxAge(60 * 60 * 24);
                response.addCookie(oldCookie);
            }
        } else {
            recipeService.viewCount(recipeNum);
            Cookie newCookie = new Cookie("recipeView", "[" + recipeNum + "]");
            newCookie.setPath("/");
            newCookie.setMaxAge(60 * 60 * 24);
            response.addCookie(newCookie);
        }
    }

    @GetMapping("/regist")
    public ModelAndView writeForm(ModelAndView mv, Authentication authentication, HttpServletRequest
            request, HttpServletResponse response) {

        List<RecipeCategoryDTO> recipeCategory = recipeService.readcategory();
        List<RecipeUnitDTO> recipeUnit = recipeService.readUnit();
        List<IngredientDTO> recipeIng = recipeService.readIng();

        mv.addObject("CategoryList", new RecipeCategoryDTO());
        mv.addObject("recipeCategory", recipeCategory);
        mv.addObject("UnitList", new RecipeUnitDTO());
        mv.addObject("recipeUnit", recipeUnit);
        mv.addObject("IngList", new IngredientDTO());
        mv.addObject("recipeIng", recipeIng);

        mv.setViewName("/recipe/write");
        return mv;
    }

    @PostMapping("/registform")
    @ResponseBody
    public ModelAndView writeReci(ModelAndView model, RecipeWriteDTO recipeWriteDTO, HttpServletRequest request,
                                  @RequestParam(value = "rpFileName", required = false) List<MultipartFile> rpFile,
                                  @RequestParam(value = "rpContent", required = false) List<String> rpContent,
                                  @RequestParam(value = "ingName", required = false) int[] ingName,
                                  @RequestParam(value = "weigh", required = false) String[] weigh,
                                  @RequestParam(value = "riUnitNum", required = false) int[] riUnitNum
    ) {
        try {
            String root = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\upload\\basic\\";
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
            List<SelectProcedure> selectProcedures = new ArrayList<>();
            List<RecipeProcedureDTO> recipeProcedureDTOS = recipeWriteDTO.getRecipeProcedureDTOList();
            List<RecipePhotoWriteDTO> recipePhotoWriteDTOList = recipeWriteDTO.getRecipePhotoWriteDTOList();
            int recipeNum = recipeWriteDTO.getRecipeNum();

            // 메인 사진
            MultipartFile mainFile = recipeWriteDTO.getMainPhotoInputFile();
            String mainPhoto = mainFile.getOriginalFilename();

            if (!mainFile.isEmpty()) {
                File profileFile = new File(root);
                System.out.println(profileFile);
                if (profileFile.exists()) {
                    profileFile.mkdir();
                }

                String mainPhotoName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + "." + mainPhoto.substring(mainPhoto.lastIndexOf(".") + 1);

                mainFile.transferTo(new File(root + "\\" + mainPhotoName));

                recipeWriteDTO.setMainPhoto(mainPhotoName);
                recipeWriteDTO.setPhotoPath("/upload/basic/");
            }

            // 요리 순서 로직
            for (int i = 0; i < rpContent.size(); i++) {
                if (rpFile.get(i).getOriginalFilename().equals("")) {
                    if (i < recipeProcedureDTOS.size()) {
                        selectProcedures.add(new SelectProcedure(recipeNum, rpContent.get(i), recipeProcedureDTOS.get(i).getRpFileName(), recipeProcedureDTOS.get(i).getRpPath()));
                    }
                } else {
                    if (i > recipeProcedureDTOS.size()) {
                        File file = new File(root + "\\");

                        if (file.exists()) {
                            file.mkdirs();
                        }
                    }

                    String newPhotoName = rpFile.get(i).getOriginalFilename();
                    String newOrdeFileName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + "." + newPhotoName.substring(newPhotoName.lastIndexOf(".") + 1);

                    rpFile.get(i).transferTo(new File(root + "\\" + newOrdeFileName));
                    selectProcedures.add(new SelectProcedure(recipeNum, rpContent.get(i), newOrdeFileName, "/upload/basic/"));


                }
            }

            // 완성 사진
            for (int i = 0; i < recipePhotoWriteDTOList.size(); i++) {
                recipePhotoWriteDTOList.get(i).setRecipeNum(recipeWriteDTO.getRecipeNum());

                String photoName = recipePhotoWriteDTOList.get(i).getRecipePhotoWriteInput().getOriginalFilename();
                if (photoName != null && !photoName.equals("")) {
                    System.out.println(photoName);

                    File file = new File(root + "//" + recipePhotoWriteDTOList.get(i).getRecipeFileName());
                    if (file.exists()) {
                        file.mkdirs();
                    }

                    String PhotoName = simpleDateFormat.format(new Date(System.currentTimeMillis())) + "." + photoName.substring(photoName.lastIndexOf(".") + 1);

                    recipePhotoWriteDTOList.get(i).getRecipePhotoWriteInput().transferTo(new File(root + "\\" + photoName));
                    recipePhotoWriteDTOList.get(i).setRecipePhotoPath("/upload/basic");
                    recipePhotoWriteDTOList.get(i).setRecipeFileName(photoName);
                }
            }

            // 재료
            List<RecipeIngredientDTO> recipeIngredientDTOS = new ArrayList<>();
            for (int i = 0; i < ingName.length; i++) {
                if (!(ingName[i] == 0)) {
                    recipeIngredientDTOS.add(new RecipeIngredientDTO(ingName[i], recipeNum, Integer.parseInt(weigh[i]), riUnitNum[i]));
                    System.out.println(ingName[i] + "이름");
                    System.out.println(weigh[i] + "용량");
                    System.out.println(riUnitNum[i] + "단위");
                }
            }

            for (RecipeIngredientDTO recipeIngredientDTO : recipeIngredientDTOS) {
                System.out.println(recipeIngredientDTO);
            }
            recipeWriteDTO.setRecipePhotoWriteDTOList(recipePhotoWriteDTOList);
            //                recipeWriteDTO.setIngredientDTOList(recipeIngredientDTOS);
            recipeWriteDTO.setRecipeProcedureDTOList(recipeProcedureDTOS);


            //                System.out.println(rpFile.get(0).getOriginalFilename());
            int result = recipeService.writeRecipe(recipeWriteDTO);


            if (result > 0) {
                model.addObject("message", "등록이 완료되었습니다.");
                model.setViewName("redirect:/view");
            } else {
                model.addObject("message", "등록에 실패하였습니다.");
                model.setViewName("redirect:/");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return model;
    }

}
