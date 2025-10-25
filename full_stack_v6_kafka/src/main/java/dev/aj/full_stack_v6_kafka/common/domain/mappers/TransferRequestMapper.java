package dev.aj.full_stack_v6_kafka.common.domain.mappers;

import dev.aj.full_stack_v6_kafka.common.domain.dtos.TransferRequestDto;
import dev.aj.full_stack_v6_kafka.common.domain.entities.TransferRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;

@Mapper(
        componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.WARN,
        unmappedSourcePolicy = ReportingPolicy.WARN
)
public interface TransferRequestMapper {

    TransferRequestDto transferRequestToTransferRequestDto(TransferRequest transferRequest);

    @Mapping(target = "id", ignore = true)
    TransferRequest transferRequestDtoToTransferRequest(TransferRequestDto transferRequestDto);

}
