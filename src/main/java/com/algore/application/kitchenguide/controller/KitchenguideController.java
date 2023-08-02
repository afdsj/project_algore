package com.algore.application.kitchenguide.controller;

import com.algore.application.kitchenguide.dto.TrimDTO;
import com.algore.application.kitchenguide.dto.TrimProcedureDTO;
import com.algore.application.kitchenguide.service.KitchenguideService;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;


import java.util.Date;
import java.util.List;

@Controller
@RequestMapping("/kitchenguide") //@RequestMapping : view의 요청 경로 지정
public class KitchenguideController {

    /* kitchenguideService는 변하지 않아야 하기 때문에 final 사용! (초기화 의무화 시키기)*/
    private final KitchenguideService kitchenguideService;

    public KitchenguideController(KitchenguideService kitchenguideService) {
        /*final은 기본값이 없기 때문에 초기화를 통해 값을 등록해 주어야함*/
        this.kitchenguideService = kitchenguideService;
    }

    /* ModelAndView
    *  스프링 프레임워크에서 사용되는 클래스로 Controller 메서드에서 데이터와 뷰 정보를 함께 담아서 클라이언트에게 전달할 때 사용
    *
    *  주요 기능과 역할
    *  1. 데이터와 뷰 정보를 함께 담기 : ModelAndView 객체는 뷰에 전달할 데이터와 뷰의 이름을 함께 저장할 수 있다
    *  2. 데이터 전달 : ModelAndView 객체는 addObject()메서드를 사용하여 데이터를 추가할 수 있다
    *                 데이터는 이름과 값의 쌍으로 저장되며 뷰에서 해당 이름으로 데이터를 사용할 수 있다
    *  3. 뷰 이름 설정 : setViewName() 메서드를 사용하여 뷰의 이름을 설정할 수 있다
    *                  Controller는 클라이언트에게 어떤 뷰를 보여줄지 결정할 수 있다
    *  4. Controller의 처리 결과 반환 : Controller 메서드는 ModelAndView 객체를 반환하여 Controller의 처리 결과를 클라이언트에게 전달한다
    *                                ModelAndView 객체에 저장된 데이터와 뷰 정보를 기반으로 뷰가 렌더링되어 클라이언트에게 응답으로 보내진다
    *
    * 렌더링 : 웹 애플리케이션에서 서버 측에서 생성된 데이터나 정보를 클라이언트측으로 전송하여 브라우저에서 시각적으로 표현하는 것을 말한다
    *         웹 페이지를 사용자에게 보여주기 위해 서버에서 생성된 데이터를 HTML,CSS,JavaScrip등의 형식으로 해석하여 브라우저에 출력하는 과정 */
    @GetMapping("/mainview") //메인 화면
    public ModelAndView mainview(ModelAndView mv, HttpServletRequest request/*요청*/, HttpServletResponse response/*응답*/) {
        /* kitchenguideService에서 mainPost()호출해서 List<TrimDTO> 타입의 dtomainList 생성*/
        List<TrimDTO> dtomainList = kitchenguideService.mainPost();
        /* addObject : 객체에 데이터를 추가하는 메서드
        *  mv 객체에 "dtomainList"라는 이름으로 dtomainList 추가하기 -> view에서 데이터를 사용할 수 있게 된다
        *  메인사진(동영상 썸네일), 제목 가져오기 */
        mv.addObject("dtomainList", dtomainList);
        /* mv 객체의 뷰 이름을 "kitchenguide/mainview"로 경로 설정 */
        mv.setViewName("/kitchenguide/mainview");
        /* mv 객체를 반환해서 결과 보여주기*/
        return mv;
    }

    //    @PreAuthorize("hasAuthority('ADMIN')")

    /* GetMapping : HTTP Get 요청이 "/trimupdate/{trimNum} 경로로 들어오면 해당 메서드 실행
    *               {trimNum}은 경로변수로 실제 요청 URL에서 해당 부분은 변수로 사용되며 @PathVariable 통해 매개변수에 바인딩된다
    *
    * 바인딩 : 프로그래밍에서 변수나 객체 등의 값을 연결하는 작업을 의미
    *         스프링 프레임워크에서 바인딩은 주로 사용자의 입력 데이터를 자바 객체에 자동으로 연결하는 작업을 말한다
    *         ex) 사용자가 웹 페이지의 폼을 작성하여 제출하면 스프링은 데이터를 자바 객체에 바인딩하여 컨트롤러에서 사용하거나
    *             서비스 계층으로 전달할 수 있다
    *
    * @PathVariable : URL 경로의 일부를 컨트롤러 메서드의 매개변수에 바인딩한다
    * '@PathVaiable("trimNum") int trimNum' : 경로 변수인 {trimNum}을 매개변수로 받아서 int 타입의 trimNum 변수에 할당한다
    *
    * Authentication : 인증 결과를 담는 객체로 사용자가 제공한 인증 정보와 인증 성공 여부등을 포함한다
    * Authentication authentication : 현재 인증된 사용자의 정보를 담고 있는 Spring Security의 Authentication 객체이다
    * */
    @GetMapping("/trimupdate/{trimNum}") //손질법 게시글 수정(관리자 권한) - 페이지 수정 폼 컨트롤러
    public ModelAndView trimupdate(ModelAndView mv, @PathVariable("trimNum") int trimNum/*손질번호*/, Authentication authentication/*권한*/) {

        /* getAuthorities() : Spring Security 프레임워크에서 제공
        *  메소드 호출하면 사용자가 가진 권한들이 담긴 정보 얻을 수 있다
        *  ex) 사용자가 "ROLE_ADMIN", "ROLE_USER" 두 개의 권한을 가지고 있으면 메서드 호출 시 [ROLE_ADMIN, ROLE_USER]로 출력*/
        System.out.println(authentication.getAuthorities());
        
        /* 파라미터를 넘겨주는 방법
          1. @PathVariable 사용
            -> (ex. localhost:8080/kitchenguide/trimupdate/1)의 형식
            -> trimNum이라는 값을 매개변수로 넘겨 쿼리 스트링 형식이 아닌 특정 숫자 그 자체로의 조회
          2. 쿼리 스트링 사용
            -> (ex. localhost:8080/kitchenguide/trimupdate?trimNum=1)의 형식 */

        /* Service 로직에서 불러오기
        *  kitchenguideService의 readTrim 메서드 호출 -> trimNum 매개변수를 받아서 해당 번호에 해당하는 TrimDTO 객체를 반환
        *  readTrim 메서드가 반환하는 TrimDTO 객체를 trimDTO 변수에 할당 -> trimDTO 변수에 TrimDTO 타입의 데이터가 저장된다*/
        TrimDTO trimDTO = kitchenguideService.readTrim(trimNum);
        /* 'List<TrimProcedureDTO> procedureList' : procedureList라는 이름의 List 변수 선언, List는 TrimProcedureDto 객체를 담기
        *  kitchenguideService의 readPost 메서드 호출 -> trimNum 매개변수를 받아서 해당 번호에 해당하는 TrimProcedureDTO 객체를 반한
        *  readPost 메서드가 반환하는 TrimProcedureDTO 객체들의 리스트를 procedureList 변수에 할당 */
        List<TrimProcedureDTO> procedureList = kitchenguideService.readPost(trimNum);

        /* 데이터 전송("변수이름", "데이터 값");
         *  html 문서에서 타임리프 ${변수이름.dto(필드}이름}  ->  이렇게 사용하기 */
        /* mv 객체에 "trimDTO"라는 이름으로 trimDTO 추가
        *  뷰에서 손질법 제목, 내용, 동영상URL 정보 사용*/
        mv.addObject("trimDTO", trimDTO);
        /* mv 객체에 "procedureList라는 이름으로 procedureList를 추가
        *  뷰에서 손질법 순서 정보 사용*/
        mv.addObject("procedureList", procedureList);

        /*기존 값 읽어오는지 확인하기...*/
        System.out.println("trimupdate Controller : " + trimDTO);
        System.out.println("trimupdate Controller : " + procedureList);

        /* mv 객체의 뷰 이름을 "kitchenguide/trimupdate"로 경로 설정*/
        mv.setViewName("/kitchenguide/trimupdate");
        /* mv 객체를 반환해서 결과를 보여주기*/
        return mv;
    }

    /* PostMapping : HTTP Post 요청이 "/trimupdate/{trimNum} 경로로 들어오면 해당 메서드 실행
    *                {trimNum}은 경로 변수로 실제 요청 URL에서 해당 부분은 변수로 사용되며 @PathVariable 어노테이션을 통해 매개변수에 바인딩 된다
    *
    * @RequestParam : 스프링 프레임워크에서 사용되는 어노테이션, HTTP 요청 파라미터와 매핑할때 사용,
    *                 클라이언트가 HTTP 요청을 보낼때 URL에 쿼리 파라미터 형태로 데이터를 전송하면 이를 서버에서 받아 처리하는 용도로 사용
    *                 요청 값 = 파라미터 이름 -> 동일한 경우에만 매개변수에 값이 매핑된다 */
    @PostMapping("/trimupdate/{trimNum}") //손질법 게시글 수정(관리자 권한) - 수정 시 작동하는 컨트롤러
    public ModelAndView trimupdatepost(ModelAndView mv/*뷰와 데이터를 설정하는데 사용될 객체*/, TrimDTO trimDTO/*손질법 정보를 담고 있는 DTO*/,
                                       List<TrimProcedureDTO> trimProcedureDTO /* 수정할 손질법 순서의 정보를 담고 있는 TrimProcedureDTO 객체들의 리스트*/,
                                       HttpServletRequest request/*요청*/, HttpServletResponse response/*응답*/,
                                       @RequestParam("trimTitle") String trimTitle/*손질제목, String trimTitle 이름의 값을 trimTitle 변수에 할당*/,
                                       @RequestParam("trimDetail") String trimDetail/*손질내용, String trimDetail 이름의 값을 trimDetail 변수에 할당*/,
                                       @RequestParam("trimVideoLink") String trimVideoLink/*동영상링크, String trimVideoLink 이름의 값을 trimVideoLink 변수에 할당*/,
                                       @RequestParam("tpDetail") String tpDetail /*손질내용 리스트에 담기, String tpDetail 이름의 값을 tpDetail 변수에 할당*/) {

//        ,
//        @RequestParam("tpFileName") List<MultipartFile> fileOne/*파일 저장해주기*/
        System.out.println("post/trimupdate controller 실행됨--------------------------------");

        /* TrimDTO 손질법 제목, 내용, 동영상URL 업데이트*/
        trimDTO.setTrimTitle(trimTitle); //손질 제목
        trimDTO.setTrimDetail(trimDetail); //손질 내용
        trimDTO.setTrimVideoLink(trimVideoLink); //동영상링크

        /*값 제대로 받아오는지 확인*/
        System.out.println("trimTitle : " + trimTitle);
        System.out.println("trimDetail : " + trimDetail);
        System.out.println("trimVideoLink : " + trimVideoLink);

        /* Service 로직에서 불러오기
        *  kitchenguideService 객체에서 trimUpdatePost 메서드를 호출해서
        *  결과를 int 타입의 result 변수에 할당*/
        int result = kitchenguideService.trimUpdatePost(trimDTO);

        System.out.println("손질순서 값 수정해보기 ------");

        /*손질순서 리스트에 담기*/
//        trimProcedureDTO.set(tpDetail);


//        String root = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\upload\\basic\\";
//        /*파일 이름 중복을 방지하기 위한 초단위 파일명*/
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
//        MultipartFile trimFile = trimProcedureDTO.getInputFile();
//        String trimFileName = trimFile.getOriginalFilename();
//
//        if (!trimFile.isEmpty()) {
//            File trimTPFile = new File(root + trimProcedureDTO.getTpFileName());
//            System.out.println(trimTPFile);
//            if (trimTPFile.exists()) {
//                trimTPFile.delete();
//            }
//
//        }


        /*파일 이름만 담아서 dto로 보내주기....*/

        /* mv 객체의 뷰 이름을 "kitchenguide/trimread"로 경로 설정*/
        mv.setViewName("/kitchenguide/trimread");
        /* mv 객체를 반환해서 결과를 보여주기*/
        return mv;
    }

    /* GetMapping : HTTP Post 요청이 "/delete/{trimNum} 경로로 들어오면 해당 메서드 실행
     *              {trimNum}은 경로 변수로 실제 요청 URL에서 해당 부분은 변수로 사용되며 @PathVariable 어노테이션을 통해 매개변수에 바인딩 된다*/
    @GetMapping("/delete/{trimNum}") //손질법 게시글 삭제
    public ModelAndView deleteTrimPost(ModelAndView mv, @PathVariable("trimNum") int deleteNum/*손질번호*/) {
        /* 값 받아오는지 확인 */
        System.out.println("con t : " + deleteNum);

        /* Service 로직에서 불러오기
        *  kitchenguideService 객체에서 deleteTrimPost 메서드를 호출
        *  deleteNum이라는 int 타입의 변수를 인자로 받아서 해당 번호에 해당하는 손질법 게시글을 삭제하는 로직을 수행*/
        int deleted = kitchenguideService.deleteTrimPost(deleteNum);

        /* mv 객체의 뷰 이름을 "redirect:/kitchenguide/mainview"로 경로 설정
        *  /kitchenguide/mainview로 redirect되면 메인 화면으로 보여주는 뷰로 이동*/
        mv.setViewName("redirect:/kitchenguide/mainview");
        /* mv 객체를 반환해서 결과를 보여주기*/
        return mv;
    }

    /* GetMapping : HTTP Post 요청이 "/trimread/{trimNum} 경로로 들어오면 해당 메서드 실행
     *              {trimNum}은 경로 변수로 실제 요청 URL에서 해당 부분은 변수로 사용되며 @PathVariable 어노테이션을 통해 매개변수에 바인딩 된다*/
    @GetMapping("/trimread/{trimNum}") //사용자가 get 방식으로 /kitchenguide/trimread를 요청할 경우 실행, {동적으로 바뀔 수 있는 값}
    public ModelAndView trimread(ModelAndView mv, @PathVariable("trimNum") int trimNum/*손질번호*/, HttpServletRequest request/*요청*/, HttpServletResponse response/*응답*/) {

        /* 조회수
        *  메서드를 호출하여 손질법 게시글의 조회수 증가시킴*/
        trimPostViewCount(request, response, trimNum);

        /* Service 로직에서 불러오기
        *  kitchenguideService 객체에서 readTrim 메서드를 호출해서 trimNum을 int 타입의 변수를 인자로 받아
        *  해당 번호에 해당하는 손질법 게시글을 조회하는 로직을 수행 */
        TrimDTO trimDTO = kitchenguideService.readTrim(trimNum);
        /* kitchenguideService에서 readPost()호출해서 List<TrimProcedureDTO> 타입의 procedureList 생성*/
        List<TrimProcedureDTO> procedureList = kitchenguideService.readPost(trimNum);

        /* 데이터 전송("변수이름", "데이터 값");
         *  html 문서에서 타임리프 ${변수이름.dto(필드}이름}  ->  이렇게 사용하기 */

        /* mv 객체에 "trimDTO"라는 이름으로 trimDTO 추가
        *  뷰에서 손질법 제목, 내용, 동영상 URL 정보 사용 */
        mv.addObject("trimDTO", trimDTO);
        /* mv 객체에 "procedureList"라는 이름으로 procedureList를 추가
        *  뷰에서 손질법 순서 정보 사용*/
        mv.addObject("procedureList", procedureList);
        /* mv 객체의 뷰 이름을 "kitchenguide/trimreadw"로 경로 설정 */
        mv.setViewName("kitchenguide/trimread");

        /* mv 객체를 반환해서 결과를 클라이언트에게 보여준다*/
        return mv;
    }

    // 손질법 게시글의 조회수를 증가시키는 부분
    private void trimPostViewCount(HttpServletRequest request, HttpServletResponse response, int trimNum/* 조회수를 증가시킬 손질법 게시글의 번호*/) {
        /* 조회수
        *  Cookie 객체를 생성하고 초기값을 null로 설정
        *  oldCookie : 변수를 찾은 view 이름을 가진 쿠키를 저장하기 위한 변수*/
        Cookie oldCookie = null;

        /* request.getCookies()를 통해 클라이언트로부터 전송된 모든 쿠키들을 배열로 받아온다
        *  클라이언트가 이전에 전송한 모든 쿠키들이 'cookies' 배열에 저장된다*/
        Cookie[] cookies = request.getCookies();
        /* cookies가 null이 아닌 경우 실행*/
        if (cookies != null) {
            /* cookies 배열에 저장된 각각의 쿠키들을 하나씩 가져온다
            *  cookie 변수에는 배열의 각 요소인 쿠키 객체가 차례대로 할당*/
            for (Cookie cookie : cookies) {
                /* 가져온 쿠키 객체의 이름(getName())이 view와 같은지 확인*/
                if (cookie.getName().equals("view")) {
                    /* view라는 이름을 가진 쿠키를 찾으면 해당 쿠키를 oldCookie 변수에 할당
                    *  oldCookie에는 view라는 이름을 가진 쿠키 객체가 저장된다*/
                    oldCookie = cookie;
                }
            }
        }
        /* oldCookie가 null이 아닌 경우 실행
        *  이전에 조회한 게시글의 쿠키가 존재하는 경우 쿠키에 해당 게시글 번호를 기록하고 조회수를 증가시키는 로직*/
        if (oldCookie != null) {
            /* 쿠키의 값에 게시글번호가 포함되어 있지 않는 경우 검사
            *  getValue() : 쿠키의 값(value)을 가져오는 메서드
            *  contains() : 쿠키의 값에 게시글 번호가 포함되어 있는지 확인하는 메서드*/
            if (!oldCookie.getValue().contains("[" + trimNum + "]")) {
                /* kitchenguideService를 통해 손질법 게시글의 조회수를 증가시키는 trimPostViewCount 메서드 호출*/
                kitchenguideService.trimPostViewCount(trimNum);
                /* 기존 쿠키(oldCookie)의 값을 업데이트하는 부분
                *  쿠키의 값 끝에 게시글번호를 추가하여 이전에 조회한 게시글 번호를 기록*/
                oldCookie.setValue(oldCookie.getValue() + "_[" + trimNum + "]");
                /* 쿠키의 경로 설정, 쿠키가 전체 서버에서 가능*/
                oldCookie.setPath("/");
                /* 쿠키의 유효 시간 설정, 해당 쿠키는 24시간 동안 유지된다*/
                oldCookie.setMaxAge(60 * 60 * 24);
                /* 업데이트된 쿠키를 응답에 추가하여 클라이언트에게 전달
                *  클라이언트는 이후에 이 쿠키를 갖고 다시 요청을 보낼 때 서버가 이전에 조회한 게시글의
                *  번호를 기록한 쿠키를 함께 전송*/
                response.addCookie(oldCookie);
            }
        /* oldCookie가 null인 경우*/
        } else {
            /* kitchenguideService를 통해 손질법 게시글의 조회수를 증가시키는 trimPostViewCount 메서드 호출*/
            kitchenguideService.trimPostViewCount(trimNum);
            /* Cookie 클래스를 이용하여 이름이 view이고 값이 게시글번호인 새로운 쿠키 생성*/
            Cookie newCookie = new Cookie("view", "[" + trimNum + "]");
            /* 쿠키의 경로 설정*/
            newCookie.setPath("/");
            /* 쿠키의 유효 시간 설정, 해당 쿠키는 24시간 동안 유지된다*/
            newCookie.setMaxAge(60 * 60 * 24);
            /* 업데이트된 쿠키를 응답에 추가하여 클라이언트에게 전달
             *  클라이언트는 이후에 이 쿠키를 갖고 다시 요청을 보낼 때 서버가 이전에 조회한 게시글의
             *  번호를 기록한 쿠키를 함께 전송*/
            response.addCookie(newCookie);
        }
    }

    // 해결 완료!
    @GetMapping("/trimwrite") //사용자가 get 방식으로 /kitchenguide/trimwrite를 요청할 경우 실행
    public String trimwrite() {
        return "kitchenguide/trimwrite";
    }

    /* ModelAndView : Controller 처리 결과 후 응답할 view와 view에 전달할 값을 저장
     *  @RequestParam : HttpServletRequest 객체와 같은 역할을 한다 (HttpServletRequest의 request.getParameter의 기능과 동일)
     *
     * "tpFileName" = html(화면) name과 동일해야 함
     * List<MultipartFile> rpFile : 클라이언트가 업로드한 파일 데이터를 받기 위한 매개변수
     * RedirectAttributes : 리다이엑트 시에 데이터를 전달하기 위한 객체
     * */
    @PostMapping("/trimwrite") //사용자가 post 방식으로 /kitchenguide/trimwrite를 요청할 경우 실행
    @ResponseBody
    public ModelAndView insertTrim(ModelAndView mv, TrimDTO trimDTO, @RequestParam(value = "tpFileName", required = false)
    List<MultipartFile> rpFile, String[] tpDetail, RedirectAttributes redirectAttributes) {
        System.out.println(trimDTO == null);
        System.out.println("1");
//      손질법 등록 확인
        System.out.println("값 넘어오는지 확인하기...-=--------------------");
        System.out.println("trimNum : " + trimDTO.getTrimNum());
        System.out.println("trimTitle : " + trimDTO.getTrimTitle());
        System.out.println("trimDetail : " + trimDTO.getTrimDetail());
        System.out.println("trimViews : " + trimDTO.getTrimViews());
        System.out.println("trimVideoLink : " + trimDTO.getTrimVideoLink());
        System.out.println("tpDetail : " + tpDetail[0]);
        System.out.println(trimDTO); // 제목, 내용, 동영상URL ok, (번호 : 0 , 조회수 : 0, 상태 : null x)

//      사진 등록 확인
        System.out.println(rpFile.get(0).getOriginalFilename());
        try {
//          현재 어플리케이션의 작업 리덱토리에서 정적 리소스 파일들을 저장할 경로를 지정
            String root = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\upload\\basic\\";
//          파일 이름 중복을 피하기 위해 현재 시간 기준으로 파일 이름을 생성할 때 사용할 날짜 형식을 지정
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
//          객체들을 저장할 리스트 생성
            List<TrimProcedureDTO> trimProcedureDTOS = new ArrayList<>();

            if (rpFile != null && tpDetail != null){
                for (int i = 0; i < rpFile.size(); i++){
                    System.out.println(tpDetail[i]);
                    if(rpFile.get(i)!=null){
                        System.out.println(tpDetail[i]);
                        MultipartFile multipartFile = rpFile.get(i);
                        // 업로드 된 파일의 원본 파일 이름 가져오기
                        String originName = multipartFile.getOriginalFilename();
                        // 이름 중복을 피하기 위해서 현재 시간을 기준으로 새로운 파일 이름 생성 (파일 이름에는 원본 파일의 확장자 포함되어 있음)
                        String newName = simpleDateFormat.format(new Date(System.currentTimeMillis()))+"."+originName.substring(originName.lastIndexOf(".")+1);
                        // 업로드 된 파일이 로컬 저장소에 새로운 파일 이름으로 저장
                        multipartFile.transferTo(new File(root+"\\"+newName));
                        // 이미지 경로 설정
                        trimProcedureDTOS.add(new TrimProcedureDTO(newName, tpDetail[i], "/upload/basic/"));
                    }
                }
            }
            // 손질법 순서 정보 저장
            trimDTO.setTrimProcedureDTOList(trimProcedureDTOS);

        } catch (Exception e) {
            e.printStackTrace();
        }

        /* KitchenguideService에 있는 insertTrim (Trim 테이블에 있는 값 넣어주기)를
         *  result에 실행 결과 담기 1 : 성공 0 : 실패*/
        int result = kitchenguideService.insertTrim(trimDTO);

        if (result > 0) {
            /* 손질법 등록 성공
             *  result가 0보다 클 때
             *  view에 전달할 값 설정 (데이터 보낼 때)
             *  mv.addObject("변수 이름", "데이터 값");
             * */
            redirectAttributes.addFlashAttribute("message", "등록이 완료되었습니다.");
            /* 응답할 view 이름 설정
             *  mv.setViewName("뷰의 경로");*/
            mv.setViewName("redirect:/kitchenguide/mainview");
        } else {
            /* 손질법 등록 실패
             * result가 0보다 크지 않을 때
             * */
            redirectAttributes.addFlashAttribute("message", "등록에 실패하였습니다.");
            mv.setViewName("redirect:/kitchenguide/trimread");
        }
        return mv;
    }


    /*
     *  보관법
     *
     * */

//    @GetMapping("/trimupdate/{trimNum}") //손질법 게시글 수정(관리자 권한) - 페이지 수정 폼 컨트롤러
//    public ModelAndView trimupdate(ModelAndView mv, @PathVariable("trimNum") int trimNum/*손질번호*/, Authentication authentication/*권한*/) {
//
//        System.out.println(authentication.getAuthorities());
//
//        /* 파라미터를 넘겨주는 방법
//          1. @PathVariable 사용
//            -> (ex. localhost:8080/kitchenguide/trimupdate/1)의 형식
//            -> trimNum이라는 값을 매개변수로 넘겨 쿼리 스트링 형식이 아닌 특정 숫자 그 자체로의 조회
//          2. 쿼리 스트링 사용
//            -> (ex. localhost:8080/kitchenguide/trimupdate?trimNum=1)의 형식 */
//
//        /* Service 로직에서 불러오기 */
//        TrimDTO trimDTO = kitchenguideService.readTrim(trimNum);
//        List<TrimProcedureDTO> procedureList = kitchenguideService.readPost(trimNum);
//
//        /* 데이터 전송("변수이름", "데이터 값");
//         *  html 문서에서 타임리프 ${변수이름.dto(필드}이름}  ->  이렇게 사용하기 */
//        mv.addObject("trimDTO", trimDTO); //손질법 제목, 내용, 동영상URl
//        mv.addObject("procedureList", procedureList); //손질법 순서
//
//        /*기존 값 읽어오는지 확인하기...*/
//        System.out.println("trimupdate Controller : " + trimDTO);
//        System.out.println("trimupdate Controller : " + procedureList);
//
//        mv.setViewName("/kitchenguide/trimupdate"); //응답할 뷰의 경로 설정 (리턴 값)
//        return mv;
//    }
//
//    @PostMapping("/trimupdate/{trimNum}") //손질법 게시글 수정(관리자 권한) - 수정 시 작동하는 컨트롤러
//    public ModelAndView trimupdatepost(ModelAndView mv, TrimDTO trimDTO, List<TrimProcedureDTO> trimProcedureDTO,
//                                       HttpServletRequest request/*요청*/, HttpServletResponse response/*응답*/,
//                                       @RequestParam("trimTitle") String trimTitle/*손질제목*/,
//                                       @RequestParam("trimDetail") String trimDetail/*손질내용*/,
//                                       @RequestParam("trimVideoLink") String trimVideoLink/*동영상링크*/,
//                                       @RequestParam("tpDetail") String tpDetail /*손질내용 리스트에 담기*/) {
//
////        ,
////        @RequestParam("tpFileName") List<MultipartFile> fileOne/*파일 저장해주기*/
//        System.out.println("post/trimupdate controller 실행됨--------------------------------");
//
//        /*TrimDTO*/
//        trimDTO.setTrimTitle(trimTitle); //손질 제목
//        trimDTO.setTrimDetail(trimDetail); //손질 내용
//        trimDTO.setTrimVideoLink(trimVideoLink); //동영상링크
//
//        /*값 제대로 받아오는지 확인*/
//        System.out.println("trimTitle : " + trimTitle);
//        System.out.println("trimDetail : " + trimDetail);
//        System.out.println("trimVideoLink : " + trimVideoLink);
//
//        /*매퍼 연결*/
//        int result = kitchenguideService.trimUpdatePost(trimDTO);
//
//        System.out.println("손질순서 값 수정해보기 ------");
//
//        /*손질순서 리스트에 담기*/
////        trimProcedureDTO.set(tpDetail);
//
//
////        String root = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\upload\\basic\\";
////        /*파일 이름 중복을 방지하기 위한 초단위 파일명*/
////        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
////        MultipartFile trimFile = trimProcedureDTO.getInputFile();
////        String trimFileName = trimFile.getOriginalFilename();
////
////        if (!trimFile.isEmpty()) {
////            File trimTPFile = new File(root + trimProcedureDTO.getTpFileName());
////            System.out.println(trimTPFile);
////            if (trimTPFile.exists()) {
////                trimTPFile.delete();
////            }
////
////        }
//
//
//        /*파일 이름만 담아서 dto로 보내주기....*/
//
//
//        mv.setViewName("/kitchenguide/trimread");
//        return mv;
//    }
//
//    @GetMapping("/delete/{trimNum}") //손질법 게시글 삭제
//    public ModelAndView deleteTrimPost(ModelAndView mv, @PathVariable("trimNum") int deleteNum/*손질번호*/) {
//
//        System.out.println("con t : " + deleteNum);
//        int deleted = kitchenguideService.deleteTrimPost(deleteNum);
//
//        mv.setViewName("redirect:/kitchenguide/mainview");
//        return mv;
//    }
//
//
//    @GetMapping("/trimread/{trimNum}") //사용자가 get 방식으로 /kitchenguide/trimread를 요청할 경우 실행, {동적으로 바뀔 수 있는 값}
//    public ModelAndView trimread(ModelAndView mv, @PathVariable("trimNum") int trimNum/*손질번호*/, HttpServletRequest request/*요청*/, HttpServletResponse response/*응답*/) {
//
//        /* 조회수 */
//        trimPostViewCount(request, response, trimNum);
//
//        /* Service 로직에서 불러오기 */
//        TrimDTO trimDTO = kitchenguideService.readTrim(trimNum);
//        List<TrimProcedureDTO> procedureList = kitchenguideService.readPost(trimNum);
//
//        /* 데이터 전송("변수이름", "데이터 값");
//         *  html 문서에서 타임리프 ${변수이름.dto(필드}이름}  ->  이렇게 사용하기 */
//        mv.addObject("trimDTO", trimDTO); //손질법 제목, 내용, 동영상URl
//        mv.addObject("procedureList", procedureList); //손질법 순서
//
//        mv.setViewName("kitchenguide/trimread"); //응답할 뷰의 경로 설정 (리턴 값)
//        return mv; //ModelAndView 객체 반환
//    }
//
//    private void trimPostViewCount(HttpServletRequest request, HttpServletResponse response, int trimNum) {
//        /* 조회수 */
//        Cookie oldCookie = null;
//
//        Cookie[] cookies = request.getCookies();
//        if (cookies != null) {
//            for (Cookie cookie : cookies) {
//                if (cookie.getName().equals("view")) {
//                    oldCookie = cookie;
//                }
//            }
//        }
//
//        if (oldCookie != null) {
//            if (!oldCookie.getValue().contains("[" + trimNum + "]")) {
//                kitchenguideService.trimPostViewCount(trimNum);
//                oldCookie.setValue(oldCookie.getValue() + "_[" + trimNum + "]");
//                oldCookie.setPath("/");
//                oldCookie.setMaxAge(60 * 60 * 24);
//                response.addCookie(oldCookie);
//            }
//        } else {
//            kitchenguideService.trimPostViewCount(trimNum);
//            Cookie newCookie = new Cookie("view", "[" + trimNum + "]");
//            newCookie.setPath("/");
//            newCookie.setMaxAge(60 * 60 * 24);
//            response.addCookie(newCookie);
//        }
//    }
//
//    @GetMapping("/trimwrite") //사용자가 get 방식으로 /kitchenguide/trimwrite를 요청할 경우 실행
//    public String trimwrite() {
//        return "kitchenguide/trimwrite";
//    }
//
//    /* ModelAndView : Controller 처리 결과 후 응답할 view와 view에 전달할 값을 저장
//     *  @RequestParam : HttpServletRequest 객체와 같은 역할을 한다 (HttpServletRequest의 request.getParameter의 기능과 동일)
//     *
//     * "tpFileName" = html(화면) name과 동일해야 함
//     * List<MultipartFile> fileName : 클라이언트가 업로드한 파일 데이터를 받기 위한 매개변수
//     * RedirectAttributes : 리다이엑트 시에 데이터를 전달하기 위한 객체
//     * */
//    @PostMapping("/trimwrite") //사용자가 post 방식으로 /kitchenguide/trimwrite를 요청할 경우 실행
//    @ResponseBody
//    public ModelAndView insertTrim(ModelAndView mv, TrimDTO trimDTO, @RequestParam(value = "tpFileName", required = false)
//    List<MultipartFile> fileName, RedirectAttributes redirectAttributes) {
//        System.out.println(trimDTO == null);
//        System.out.println("1");
////      손질법 등록 확인
//        System.out.println("값 넘어오는지 확인하기...-=--------------------");
//        System.out.println("trimNum : " + trimDTO.getTrimNum());
//        System.out.println("trimTitle : " + trimDTO.getTrimTitle());
//        System.out.println("trimDetail : " + trimDTO.getTrimDetail());
//        System.out.println("trimViews : " + trimDTO.getTrimViews());
//        System.out.println("trimVideoLink : " + trimDTO.getTrimVideoLink());
//        System.out.println(trimDTO); // 제목, 내용, 동영상URL ok, (번호 : 0 , 조회수 : 0, 상태 : null x)
//
////         사진 등록 확인
//        System.out.println(fileName.get(0).getOriginalFilename());
//        try {
////          현재 어플리케이션의 작업 리덱토리에서 정적 리소스 파일들을 저장할 경로를 지정
//            String root = System.getProperty("user.dir") + "\\src\\main\\resources\\static\\upload\\basic\\";
////          파일 이름 중복을 피하기 위해 현재 시간 기준으로 파일 이름을 생성할 때 사용할 날짜 형식을 지정
//            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSSS");
////          객체들을 저장할 리스트 생성
////            List<TrimProcedureDTO> trimProcedureDTOS = new ArrayList<>();
////          TrimDTO 객체에서 trimProcedureDTOList 필드를 가져와서 TrimProcedureDTOList 객체들을 저장
////          List<TrimProcedureDTO> trimProcedureDTOList = trimDTO.getTrimProcedureDTOList();
////          TrimDTO 객체에서 trimNum 필드를 가져와 손질 번호 저장
//            int trimNum = trimDTO.getTrimNum();
//
//            /* 사진 로직
//             *  MultipartFile : 파일 업로드 시 클라이언트로부터 전송된 파일 데이터를 처리하는 인터페이스
//             *  리스트의 첫 번째 업로드된 파일을 가져오고 multipartFile에 할당*/
////            MultipartFile multipartFile = fileName.get(0);
////            // 업로드된 파일의 원본 파일 이름 가져오기
////            String name = multipartFile.getOriginalFilename();
////            // 이름 중복을 피하기 위해서 현재 시간을 기준으로 새로운 파일 이름 생성 (파일 이름에는 원본 파일의 확장자 포함되어 있음)
////            String tpFileNames = simpleDateFormat.format(new Date(System.currentTimeMillis()))+"."+name.substring(name.lastIndexOf(".")+1);
////            System.out.println(tpFileNames);
////            // 업로드된 파일이 로컬 저장소에 새로운 파일 이름으로 저장
////            multipartFile.transferTo(new File(root+"\\"+tpFileNames));
////
////            TrimProcedureDTO trimProcedureDTO = new TrimProcedureDTO();
////            trimProcedureDTO.getTpFileName(tpFileNames); // 새로운 파일 이름으로 설정
////            trimProcedureDTO.setTpPath("/upload/basic/"); // 이미지 경로 설정
////
////            // TrimDTO 객체의 trimProcedureDTOList에 생성한 TrimProcedureDTO 객체 추가
////            trimDTO.getTrimProcedureDTOList().add(trimProcedureDTO);
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//        /* KitchenguideService에 있는 insertTrim (Trim 테이블에 있는 값 넣어주기)를
//         *  result에 실행 결과 담기 1 : 성공 0 : 실패*/
//        int result = kitchenguideService.insertTrim(trimDTO);
//
//        if (result > 0) {
//            /* 손질법 등록 성공
//             *  result가 0보다 클 때
//             *  view에 전달할 값 설정 (데이터 보낼 때)
//             *  mv.addObject("변수 이름", "데이터 값");
//             * */
//            mv.addObject("message", "등록이 완료되었습니다.");
//            /* 응답할 view 이름 설정
//             *  mv.setViewName("뷰의 경로");*/
//            mv.setViewName("redirect:kitchenguide/trimread");
//        } else {
//            /* 손질법 등록 실패
//             * result가 0보다 크지 않을 때
//             * */
//            mv.addObject("message", "등록에 실패하였습니다.");
//            mv.setViewName("redirect:kitchenguide/trimread");
//        }
//        return mv;
//    }
}