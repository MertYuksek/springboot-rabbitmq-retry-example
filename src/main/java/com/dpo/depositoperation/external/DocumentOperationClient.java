package com.dpo.depositoperation.external;

import org.springframework.cloud.openfeign.FeignClient;

@FeignClient(value = "DocumentOperationClient", url = "https://jsonplaceholder.typicode.com/")
public interface DocumentOperationClient {


}
