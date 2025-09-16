// Add this public method to your existing CategoryService class

public CategoryDTO convertToDTO(Category category) {
    CategoryDTO dto = new CategoryDTO();
    dto.setId(category.getId());
    dto.setName(category.getName());
    dto.setDescription(category.getDescription());
    dto.setImageUrl(category.getImageUrl());
    dto.setActive(category.getActive());
    dto.setCreatedAt(category.getCreatedAt());
    dto.setUpdatedAt(category.getUpdatedAt());
    return dto;
}