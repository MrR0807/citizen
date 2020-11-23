package com.good.citizen.shared;

import com.good.citizen.exceptions.ApiExceptionDetails;

public interface Result {

    class Success implements Result {
    }

    class Error implements Result {
        private final ApiExceptionDetails details;

        public Error(String message, String fieldName) {
            this.details = new ApiExceptionDetails("400", message, fieldName);
        }

        public Error(String message) {
            this.details = new ApiExceptionDetails("400", message, "");
        }

        public ApiExceptionDetails getDetails() {
            return this.details;
        }
    }
}