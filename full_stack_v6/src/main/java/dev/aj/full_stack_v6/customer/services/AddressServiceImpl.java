package dev.aj.full_stack_v6.customer.services;

import dev.aj.full_stack_v6.customer.AddressService;
import dev.aj.full_stack_v6.customer.repositories.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressServiceImpl implements AddressService {

    private final AddressRepository addressRepository;

}
