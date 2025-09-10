package com.safari_store.ecommerce.users.service;

import com.safari_store.ecommerce.users.User;
import com.safari_store.ecommerce.users.dtos.request.AddressUpdateRequest;
import com.safari_store.ecommerce.users.dtos.request.UserProfileUpdateRequest;
import com.safari_store.ecommerce.users.dtos.response.*;
import com.safari_store.ecommerce.users.models.Address;
import com.safari_store.ecommerce.users.repository.AddressRepository;
import com.safari_store.ecommerce.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.awt.print.Pageable;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    public User getCurrentUser(){
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null || !authentication.isAuthenticated()) {
            throw new RuntimeException("No authenticated user found");
        }

        String username = authentication.getName();
        return userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() ->  new RuntimeException("Current user is not found: " + username));
    }

    @Transactional(readOnly = true)
    public UserResponse getUserProfile(){
        User user = getCurrentUser();
        return mapToUserResponse(user);
    }

    @Transactional(readOnly = true)
    public PaginatedUsersResponse getAllUsers(int page, int size){
        Pageable pageable = (Pageable) PageRequest.of(page, size, Sort.by("dateJoined").descending());
        Page<User> userPage = userRepository.findAll((org.springframework.data.domain.Pageable) pageable);

        List<UserSummaryResponse> users = userPage.getContent().stream()
                .map(this::mapToUserSummaryResponse)
                .collect(Collectors.toList());

        return PaginatedUsersResponse.builder()
                .count(userPage.getTotalElements())
                .results(users)
                .next(userPage.hasNext() ? buildPageUrl(page + 1, size) : null)
                .previous(userPage.hasPrevious() ? buildPageUrl(page -1 ,size) : null)
                .build();
    }

    @Transactional
    public void deleteAccount(){
        User user = getCurrentUser();
        userRepository.delete(user);
    }

    @Transactional(readOnly = true)
    public UserResponse updateUserProfile(UserProfileUpdateRequest request){
        User user = getCurrentUser();

        if (request.getFirstName() != null){
            request.setFirstName(request.getFirstName());
        }
        if (request.getLastName() != null){
            request.setLastName(request.getLastName());
        }
        if (request.getEmail() != null && !request.getEmail().equals(user.getEmail())){
            if (userRepository.existsByEmail(request.getEmail())){
                throw new RuntimeException("Email already exists");
            }
            user.setEmail(request.getEmail());
        }
        if (request.getPhoneNumber() != null){
            if (!request.getPhoneNumber().equals(user.getPhoneNumber()) &&
            userRepository.existsByPhoneNumber(request.getPhoneNumber())){
                throw new RuntimeException("Phone number already exists");
            }
            user.setPhoneNumber(request.getPhoneNumber());
        }
        if (request.getNationalId() != null){
            if (!request.getNationalId().equals(user.getNationalId()) &&
            userRepository.existsByNationalId(request.getNationalId())){
                throw new RuntimeException("National id already exists");
            }
            user.setNationalId(request.getNationalId());
        }
        if (request.getDateOfBirth()!= null){
            user.setDateOfBirth(request.getDateOfBirth());
        }
        if (request.getGender() != null){
            user.setGender(request.getGender());
        }

        if (request.getAddresses() != null && !request.getAddresses().isEmpty()){
            updateUserAddresses(user,request.getAddresses());
        }

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    private UserResponse mapToUserResponse(User user) {
        List<AddressResponse> addresses = user.getAddresses() != null ?
                user.getAddresses().stream()
                        .map(this::mapToAddressResponse)
                        .collect(Collectors.toList()) : List.of();

        UserProfileResponse profile = UserProfileResponse.builder()
                .phoneNumber(user.getPhoneNumber())
                .nationalId(user.getNationalId())
                .dateOfBirth(user.getDateOfBirth())
                .gender(user.getGender())
                .profileImage(user.getProfileImageUrl())
                .build();

        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .dateJoined(user.getDateJoined())
                //rofile(profile)
                .userAddresses(addresses)
                .build();
    }

    private AddressResponse mapToAddressResponse(Address address) {
        return AddressResponse.builder()
                .id(address.getId())
                .addressType(address.getAddressType())
                .country(address.getCountry())
                .county(address.getCounty())
                .constituency(address.getConstituency())
                .town(address.getTown())
                .estate(address.getEstate())
                .street(address.getStreet())
                .landmark(address.getLandmark())
                .postalCode(address.getPostalCode())
               //createdAt(address.getCreatedAt())
                .updatedAt(address.getUpdatedAt())
                .build();
    }

    private String buildPageUrl(int page, int size) {
        return ServletUriComponentsBuilder.fromCurrentRequest()
                .replaceQueryParam("page", page)
                .replaceQueryParam("size", size)
                .toUriString();
    }
    private UserSummaryResponse mapToUserSummaryResponse(User user) {
        return UserSummaryResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .profileImage(user.getProfileImageUrl())
                .build();
    }


    private void updateUserAddresses(User user, List<AddressUpdateRequest> addressRequests) {
        for (AddressUpdateRequest addressRequest : addressRequests) {
            Optional<Address> existingAddress = addressRepository.findByIdAndUserId(
                    addressRequest.getId(),user.getId()
            );
            if (existingAddress.isPresent()) {
                Address address = existingAddress.get();
                updateAddressFields(address,addressRequest);
                addressRepository.save(address);
            }
            else {
                Address newAddress = new Address();
                newAddress.setUser(user);
                updateAddressFields(newAddress,addressRequest);
                addressRepository.save(newAddress);
            }
        }
    }

    private void updateAddressFields(Address address, AddressUpdateRequest request) {
        if (request.getAddressType() != null) {
            address.setAddressType(request.getAddressType());
        }
        if (request.getCounty() != null) {
            address.setCounty(request.getCounty());
        }
        if (request.getConstituency() != null) {
            address.setConstituency(request.getConstituency());
        }
        if (request.getTown() != null) {
            address.setTown(request.getTown());
        }
        if (request.getEstate() != null) {
            address.setEstate(request.getEstate());
        }
        if (request.getStreet() != null) {
            address.setStreet(request.getStreet());
        }
        if (request.getLandmark() != null) {
            address.setLandmark(request.getLandmark());
        }
        if (request.getPostalCode() != null) {
            address.setPostalCode(request.getPostalCode());
        }
        if (request.getCountry() != null) {
            address.setCountry(request.getCountry());
        }
    }
}
