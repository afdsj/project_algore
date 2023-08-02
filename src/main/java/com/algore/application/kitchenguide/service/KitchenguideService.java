package com.algore.application.kitchenguide.service;

import com.algore.application.kitchenguide.dao.KitchenguideMapper;
import com.algore.application.kitchenguide.dto.TrimDTO;
import com.algore.application.kitchenguide.dto.TrimProcedureDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service //어노테이션을 붙이지 않을 경우 bean으로 등록해주지 않음
public class KitchenguideService {

    /* KitchenguideMapper는 변하지 않아야 하기 때문에 final 사용! (초기화 의무화 시키기)*/
    private final KitchenguideMapper mapper;

    public KitchenguideService(KitchenguideMapper mapper) {
        /*final은 기본값이 없기 때문에 초기화를 통해 값을 등록해 주어야함*/
        this.mapper = mapper;
    }

    public TrimDTO readTrim(int trimNum/*조회할 손질법 게시글번호*/) {
        /* mapper에 있는 trimPostRead(trimNum) 메서드 호출해서 trimDTO에 결과 담기 */
        TrimDTO trimDTO = mapper.trimPostRead(trimNum);
        /* 조회한 게시글 정보가 담긴 trimDTO를 반환*/
        return trimDTO;
    }

    /* 손질법 게시글에 속하는 손질법 순서 조회하는 메서드*/
    public List<TrimProcedureDTO> readPost(int trimNum) {
        /* mapper에 있는 trimProPostRead 메서드 호출해서 procedureList에 결과 담기*/
        List<TrimProcedureDTO> procedureList = mapper.trimProPostRead(trimNum);
        /* 손질법 순서들의 정보가 담긴 procedureList 결과 반환*/
        return procedureList;
    }

    /* @Transactional : 스프링 프레임워크에서 제공하는 어노테이션, 데이터베이스 트랜잭션 관리를 위해서 사용 */
    @Transactional
    public int insertTrim(TrimDTO trimDTO) {
        /* 손질법 등록
         *  KitchenguideMapper에 있는 insertTrim (Trim 테이블에 있는 값 넣어주기)를
         *  result에 실행 결과 담기 1 : 성공 0 : 실패 */
        int result = mapper.insertTrim(trimDTO);

        if (result > 0) {
            /* result가 0보다 클 때 (즉 손질법이 등록 되었을 때)
             *
             *  손질법순서 등록 후 result에 실행 결과 담기
             * */
            // 손질법 순서 객체 담고 있는 리스트 = trim 객체에서 손질법 순서(TrimProcedureDTO) 객체들을 가져오는 메서드
            List<TrimProcedureDTO> trimProcedureDTOList = trimDTO.getTrimProcedureDTOList();
            /* trimProcedureDTOList null이 아니고 값이 비어있지 않을 경우 실행*/
            if (trimProcedureDTOList != null && !trimProcedureDTOList.isEmpty()) {
                /* mapper에 있는 insertTrimProduce 메서드 호출*/
                result = mapper.insertTrimProduce(trimProcedureDTOList);
            }
        }
        /* 손질법 등록 결과 반환 -> 등록 결과를 호출한 곳으로 전달*/
        return result;
    }


    public int trimPostViewCount(int trimNum/* 조회수를 증가시킬 손질법 게시글의 번호*/) {
        /* mapper에 있는 trimPostViewCount 메서드 호출해서 result에 결과 담기*/
        int result = mapper.trimPostViewCount(trimNum);
        /* 조회수를 증가시킨 후에 trimNum를 반환*/
        return trimNum;
    }

    /* 메인화면에 보여줄 손질법 정보 조회하는 메서드*/
    public List<TrimDTO> mainPost() {
        /* mapper에 있는 trimPostMain 메서드를 호출해서 dtomainList에 결과 담기*/
        List<TrimDTO> dtomainList = mapper.trimPostMain();
        /* 조회한 손질법 정보가 담긴 dtomainList를 반환*/
        return dtomainList;
    }

    public int deleteTrimPost(int trimNum/* 삭제할 손질법 번호*/) {
        /* mapper에 있는 trimPostDelete 메서드 호출해서 deleted에 결과 담기*/
        int deleted = mapper.trimPostDelete(trimNum);
        /* 게시글을 삭제하고 trimNum를 반환*/
        return trimNum;
    }

    public int trimUpdatePost(TrimDTO trimDTO/*수정할 손질법 정보*/) {
        /* mapper에 있는 trimPostUpdate 메서드 호출해서 result에 결과 담기*/
        int result = mapper.trimPostUpdate(trimDTO);
        /* 게시글을 수정하고 result에 결과 반환*/
        return result;
    }
}


