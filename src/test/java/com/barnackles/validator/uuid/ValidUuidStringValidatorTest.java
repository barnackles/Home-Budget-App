package com.barnackles.validator.uuid;

import org.junit.jupiter.api.Test;

import javax.validation.ConstraintValidatorContext;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidUuidStringValidatorTest {

    ConstraintValidatorContext constraintValidatorContext;
    @Test
    void isValid() {

        // correct uid's
        List<String> uuids = List.of(
                "94c72d57-a094-4766-86e0-225056969c90",
                "e32142a6-404b-416e-bf64-3e188a65b34e",
                "8ac22681-a5e4-4151-8691-1b4b41d7f258",
                "92d5089b-ffb8-4ae1-b5fd-63bc230e058d",
                "9a7736fd-bd2c-4be0-88b3-047471fa713c",
                "6e55e686-4f09-42fb-b0dc-1fda79121859",
                "27b04a83-a8d9-4bd0-ac3b-40f5976a5b9c",
                "5ee8b031-cb2f-493d-be83-9e96c2fd71ec",
                "cd3cf4b4-0468-47fa-b1e2-02c4cd84d608",
                "175d283b-c612-4773-84e5-4f2491f20c22",
                "02613892-7eae-4806-8d20-25cfa5ff8245",
                "e16fe839-8f38-4daa-83b4-e1e912bcdc51",
                "09673c3c-ca32-4446-9722-cbf1bd707bae",
                "6c69d5d1-a9ab-4f03-a461-b5fa3d8239e1",
                "cf0cb8c5-6564-443b-9ba9-ee1e398868e7"
        );

        List<String> notUuids = List.of(
                "94c72d57-a0944766-86e0-225056969c90",
                "e32142a6-404b-416e-b4-3e188a65b34e",
                "8ac22681-a5e4-4151-86918",
                "-ffb8-4ae1-b5fd-63bc230e058d",
                "",
                "42fb-b0dc-1fda79121859",
                "-4bd0-ac3b-40f5976a5b9c",
                "5fwefeffeee8b031-cb2f-493d-be83-9e96c2fd71ec",
                "cd3cf4b4-fgegw0468-47fa-b1e2-02c4cd84d608",
                "175d283b-c612-4773-8wegwg4e5-4f2491f20c22",
                "02613892-7eae-4gew806-8d20X25cfa5ff8245",
                "trolls",
                "huewhfuwf",
                "9w4v1w6v4wevwe-e*-g4364g4g484g684e12w1v5e6w",
                "UVH UBFH#W(*UBIW$JLKGH(*)OIEVOKWGLMMLUGBOWILK:EGLml;WEGb"
        );

        ValidUuidStringValidator validUuidStringValidator = new ValidUuidStringValidator();

        uuids.forEach(u -> assertTrue(validUuidStringValidator.isValid(u, constraintValidatorContext)));
        notUuids.forEach(n -> assertFalse(validUuidStringValidator.isValid(n, constraintValidatorContext)));

    }
}