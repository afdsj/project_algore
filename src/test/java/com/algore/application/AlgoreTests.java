package com.algore.application;

import com.algore.application.user.controller.JoinController;
import com.algore.application.user.service.JoinService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class AlgoreTests {

    @Autowired
    private JoinService joinService;


    @Test
    @DisplayName("1. 회원 이름 중복 테스트")
    void userTest(){

        // given


        // when


        //then
    }


}
