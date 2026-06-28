package com.lending.app.customer.mapper;

import com.lending.app.customer.api.dto.*;
import com.lending.app.customer.domain.model.BillingProfile;
import com.lending.app.customer.domain.model.Customer;
import com.lending.app.customer.domain.model.LoanLimit;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CustomerMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "active", constant = "true")
    @Mapping(target = "loanLimit", ignore = true)
    @Mapping(target = "billingProfile", ignore = true)
    Customer toEntity(CustomerRequest request);

    @Mapping(source = "fullName", target = "fullName")
    @Mapping(target = "loanLimit", expression = "java(toLoanLimitResponse(customer.getLoanLimit()))")
    @Mapping(target = "billingProfile", expression = "java(toBillingProfileResponse(customer.getBillingProfile()))")
    CustomerResponse toResponse(Customer customer);

    List<CustomerResponse> toResponseList(List<Customer> customers);

    default LoanLimitResponse toLoanLimitResponse(LoanLimit limit) {
        if (limit == null) return null;
        return new LoanLimitResponse(
                limit.getId(),
                limit.getMaxSingleLoan(),
                limit.getMaxTotalOutstanding(),
                limit.getMaxActiveLoans(),
                limit.getLastReviewed()
        );
    }

    default BillingProfileResponse toBillingProfileResponse(BillingProfile profile) {
        if (profile == null) return null;
        return new BillingProfileResponse(
                profile.getId(),
                profile.getBillingDay()
        );
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "version", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "loanLimit", ignore = true)
    @Mapping(target = "billingProfile", ignore = true)
    void updateEntityFromRequest(CustomerRequest request, @MappingTarget Customer customer);
}
