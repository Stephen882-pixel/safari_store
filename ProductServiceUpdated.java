// Add this public method to your existing ProductService class

public ProductDTO convertToDTO(Product product) {
    ProductDTO dto = new ProductDTO();
    dto.setId(product.getId());
    dto.setName(product.getName());
    dto.setDescription(product.getDescription());
    dto.setPrice(product.getPrice());
    dto.setStockQuantity(product.getStockQuantity());
    dto.setImageUrl(product.getImageUrl());
    dto.setAdditionalImages(product.getAdditionalImages());
    dto.setActive(product.getActive());
    dto.setFeatured(product.getFeatured());
    dto.setCategoryId(product.getCategory().getId());
    // Note: Avoid circular reference by not setting category DTO here in this context
    dto.setTags(product.getTags());
    dto.setCreatedAt(product.getCreatedAt());
    dto.setUpdatedAt(product.getUpdatedAt());
    return dto;
}