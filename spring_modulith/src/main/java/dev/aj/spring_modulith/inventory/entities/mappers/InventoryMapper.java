package dev.aj.spring_modulith.inventory.entities.mappers;

import dev.aj.spring_modulith.inventory.dtos.InventoryEntityDto;
import dev.aj.spring_modulith.inventory.entities.Inventory;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface InventoryMapper {

    InventoryEntityDto entityToDto(Inventory inventory);

    @Mapping(target = "id", ignore = true)
    Inventory dtoToEntity(InventoryEntityDto inventoryEntityDto);

}
