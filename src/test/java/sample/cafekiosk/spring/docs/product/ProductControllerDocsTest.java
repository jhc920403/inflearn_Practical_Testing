package sample.cafekiosk.spring.docs.product;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.operation.preprocess.Preprocessors;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.restdocs.payload.PayloadDocumentation;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import sample.cafekiosk.spring.api.controller.product.ProductController;
import sample.cafekiosk.spring.api.controller.product.request.ProductCreateRequest;
import sample.cafekiosk.spring.api.service.product.ProductService;
import sample.cafekiosk.spring.api.service.product.request.ProductCreateServiceRequest;
import sample.cafekiosk.spring.api.service.product.response.ProductResponse;
import sample.cafekiosk.spring.docs.RestDocsSupport;

import static sample.cafekiosk.spring.domain.product.ProductStatus.SELLING;
import static sample.cafekiosk.spring.domain.product.ProductType.HANDMADE;

public class ProductControllerDocsTest extends RestDocsSupport {

    private final ProductService productService = Mockito.mock(ProductService.class);

    @Override
    protected Object initController() {
        return new ProductController(productService);
    }

    @Test
    @DisplayName("신규 상품을 등록하는 API")
    void createProduct() throws Exception {

        ProductCreateRequest request = ProductCreateRequest.builder()
                .productType(HANDMADE)
                .productStatus(SELLING)
                .productName("아메리카노")
                .productPrice(4000)
                .build();

        BDDMockito.given(productService.createProduct(BDDMockito.any(ProductCreateServiceRequest.class)))
                        .willReturn(ProductResponse.builder()
                                .id(1L)
                                .productNo("001")
                                .productType(HANDMADE)
                                .productStatus(SELLING)
                                .productName("아메리카노")
                                .productPrice(4000)
                                .build()
                        );

        // When & Then
        mockMvc.perform(
                        MockMvcRequestBuilders.post("/api/v1/products/new")
                                .content(objectMapper.writeValueAsString(request))
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andDo(MockMvcResultHandlers.print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andDo(MockMvcRestDocumentation.document(
                        "product-create"
                        , Preprocessors.preprocessRequest(Preprocessors.prettyPrint())
                        , Preprocessors.preprocessResponse(Preprocessors.prettyPrint())
                        , PayloadDocumentation.requestFields(
                                PayloadDocumentation.fieldWithPath("productType").type(JsonFieldType.STRING).description("상품 타입")
                                , PayloadDocumentation.fieldWithPath("productStatus").type(JsonFieldType.STRING).description("상품 판매상태").optional()
                                , PayloadDocumentation.fieldWithPath("productName").type(JsonFieldType.STRING).description("상품 이름")
                                , PayloadDocumentation.fieldWithPath("productPrice").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                        , PayloadDocumentation.responseFields(
                                PayloadDocumentation.fieldWithPath("code").type(JsonFieldType.NUMBER).description("코드")
                                , PayloadDocumentation.fieldWithPath("status").type(JsonFieldType.STRING).description("상태")
                                , PayloadDocumentation.fieldWithPath("message").type(JsonFieldType.STRING).description("메시지")
                                , PayloadDocumentation.fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터")
                                , PayloadDocumentation.fieldWithPath("data.id").type(JsonFieldType.NUMBER).description("상품 ID")
                                , PayloadDocumentation.fieldWithPath("data.productNo").type(JsonFieldType.STRING).description("상품 번호")
                                , PayloadDocumentation.fieldWithPath("data.productType").type(JsonFieldType.STRING).description("상품 타입")
                                , PayloadDocumentation.fieldWithPath("data.productStatus").type(JsonFieldType.STRING).description("상품 판매 상태")
                                , PayloadDocumentation.fieldWithPath("data.productName").type(JsonFieldType.STRING).description("상품 이름")
                                , PayloadDocumentation.fieldWithPath("data.productPrice").type(JsonFieldType.NUMBER).description("상품 가격")
                        )
                ));
    }
}
