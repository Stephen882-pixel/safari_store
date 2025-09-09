package com.safari_store.ecommerce.users.service;

import com.safari_store.ecommerce.users.User;
import com.safari_store.ecommerce.users.dtos.request.AddressRequest;
import com.safari_store.ecommerce.users.dtos.response.AddressResponse;
import com.safari_store.ecommerce.users.models.Address;
import com.safari_store.ecommerce.users.repository.AddressRepository;
import com.safari_store.ecommerce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

    private final AddressRepository addressRepository;
    private final UserService userService;


    @Transactional(readOnly = true)
    public List<AddressResponse> getUserAddresses(){
        User currentUser = userService.getCurrentUser();
        List<Address> addresses = addressRepository.findByUserOrderByCreatedAtDesc(currentUser);

        return addresses.stream()
                .map(this::mapToAddressResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public AddressResponse createAddress(AddressRequest request){
        User currentUser = userService.getCurrentUser();

        Address address = new Address();
        address.setUser(currentUser);
        address.setAddressType(request.getAddressType());
        address.setCountry(request.getCountry());
        address.setCounty(request.getCounty());
        address.setConstituency(request.getConstituency());
        address.setTown(request.getTown());
        address.setStreet(request.getStreet());
        address.setEstate(request.getEstate());
        address.setPostalCode(request.getPostalCode());
        address.setLandmark(request.getLandmark());

        Address savedAddress = addressRepository.save(address);
        return mapToAddressResponse(savedAddress);
    }

    @Transactional(readOnly = true)
    public AddressResponse getAddress(Long addressId){
        User currentUser = userService.getCurrentUser();
        Address address = addressRepository.findByIdAndUserId(addressId,currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Address not found"));

        return mapToAddressResponse(address);
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .country(address.getCountry())
                .county(address.getCounty())
                .constituency(address.getConstituency())
                .estate(address.getEstate())
                .landmark(address.getLandmark())
                .town(address.getTown())
                .street(address.getStreet())
                .postalCode(address.getPostalCode())
                .createdAt(address.getCreatedDate())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

}
