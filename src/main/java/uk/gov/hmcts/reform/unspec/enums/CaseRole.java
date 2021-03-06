package uk.gov.hmcts.reform.unspec.enums;

import lombok.Getter;

@Getter
public enum CaseRole {
    CREATOR,
    APPLICANTSOLICITORONE,
    APPLICANTSOLICITORTWO,
    RESPONDENTSOLICITORONE,
    RESPONDENTSOLICITORTWO;

    private String formattedName;

    CaseRole() {
        this.formattedName = String.format("[%s]", name());
    }
}
