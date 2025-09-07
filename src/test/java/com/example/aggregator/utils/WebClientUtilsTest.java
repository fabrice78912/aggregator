package com.example.aggregator.utils;

import static org.mockito.Mockito.*;

class WebClientUtilsTest {

  /*@Test
  void testSendGetRequest_Success() {
      WebClient mockClient = mock(WebClient.class, RETURNS_DEEP_STUBS);

      @SuppressWarnings("unchecked")
      RequestHeadersUriSpec<?> uriSpecMock = mock(RequestHeadersUriSpec.class);

      when(mockClient.get()).thenReturn(uriSpecMock);
      // Cast à RequestHeadersUriSpec<?> pour résoudre l’ambiguïté
      when(((RequestHeadersUriSpec<?>) uriSpecMock).uri(any(String.class), any(Object[].class)))
              .thenReturn(uriSpecMock);
      when(uriSpecMock.exchangeToFlux(any()))
              .thenAnswer(invocation -> Flux.just(new TestDto("value1")));

      Flux<TestDto> result = WebClientUtils.sendGetRequest(
              mockClient,
              "/api/test?param={id}",
              "client1",
              TestDto.class,
              "TestService"
      );

      StepVerifier.create(result)
              .expectNextMatches(dto -> "value1".equals(dto.getName()))
              .verifyComplete();
  }

  @Test
  void testSendGetRequest_ServiceUnavailable() {
      WebClient mockClient = mock(WebClient.class, RETURNS_DEEP_STUBS);

      @SuppressWarnings("unchecked")
      RequestHeadersUriSpec<?> uriSpecMock = mock(RequestHeadersUriSpec.class);

      when(mockClient.get()).thenReturn(uriSpecMock);
      when(((RequestHeadersUriSpec<?>) uriSpecMock).uri(any(String.class), any(Object[].class)))
              .thenReturn(uriSpecMock);
      when(uriSpecMock.exchangeToFlux(any()))
              .thenReturn(Flux.error(new RuntimeException("503 Service Unavailable")));

      Flux<TestDto> result = WebClientUtils.sendGetRequest(
              mockClient,
              "/api/test?param={id}",
              "client1",
              TestDto.class,
              "TestService"
      );

      StepVerifier.create(result)
              .expectNextCount(0)
              .verifyComplete();
  }

  @Test
  void testSendGetRequest_GenericError() {
      WebClient mockClient = mock(WebClient.class, RETURNS_DEEP_STUBS);

      @SuppressWarnings("unchecked")
      RequestHeadersUriSpec<?> uriSpecMock = mock(RequestHeadersUriSpec.class);

      when(mockClient.get()).thenReturn(uriSpecMock);
      when(((RequestHeadersUriSpec<?>) uriSpecMock).uri(any(String.class), any(Object[].class)))
              .thenReturn(uriSpecMock);
      when(uriSpecMock.exchangeToFlux(any()))
              .thenReturn(Flux.error(new RuntimeException("Unexpected Error")));

      Flux<TestDto> result = WebClientUtils.sendGetRequest(
              mockClient,
              "/api/test?param={id}",
              "client1",
              TestDto.class,
              "TestService"
      );

      StepVerifier.create(result)
              .expectNextCount(0)
              .verifyComplete();
  }

  static class TestDto {
      private final String name;

      public TestDto(String name) { this.name = name; }
      public String getName() { return name; }
  }*/
}
