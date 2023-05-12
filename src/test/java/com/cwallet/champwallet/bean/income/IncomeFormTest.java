//package com.cwallet.champwallet.bean.income;
//
//import com.cwallet.champwallet.models.income.IncomeType;
//import com.cwallet.champwallet.repository.income.IncomeRepository;
//import org.junit.jupiter.api.Assertions;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.data.jdbc.DataJdbcTest;
//import static org.hamcrest.Matchers.is;
//import static org.hamcrest.MatcherAssert.assertThat;
//import static org.junit.Assert.*;
//import static org.junit.matchers.JUnitMatchers.*;
//
//import static org.junit.jupiter.api.Assertions.*;
//@DataJdbcTest
//class IncomeFormTest {
//    @Autowired
//    private IncomeRepository incomeRepository;
//
//    @Test
//    void saveIncomeTest(){
//        IncomeForm income = new IncomeForm("jerome")
//                .source("jerome")
//                .incomeTypeID(new IncomeType())
//                .description("this test")
//                .amount(Double.parseDouble(("4000")))
//                .build();
//       int result = incomeRepository.save(income);
//       assertTrue(result > 0);
//    }
//
//}