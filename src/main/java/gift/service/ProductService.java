package gift.service;


import gift.dto.ProductDto;
import gift.exception.RepositoryRelatedException;
import gift.model.product.Category;
import gift.model.product.Product;
import gift.model.product.ProductName;
import gift.repository.CategoryRepository;
import gift.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    public boolean addNewProduct(ProductDto productDto){
        if (productRepository.existsByName(productDto.name())) {
            throw new RepositoryRelatedException("Product with name " + productDto.name() + " already exists.");
        }
        Category category = findCategory(productDto.categoryName());
        Product product = new Product(category,new ProductName(productDto.name()),productDto.price(),productDto.imageUrl(),productDto.amount());

        productRepository.save(product);
        return true;
    }

    public boolean updateProduct(Long id, ProductDto productDto) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()){
            Product updateProduct = product.get();
            Category category = findCategory(productDto.categoryName());

            Product newProduct = new Product(category,new ProductName(productDto.name()),productDto.price(),productDto.imageUrl(),productDto.amount());
            updateProduct.updateProduct(newProduct);
            productRepository.save(updateProduct);
            return true;
        }
        return false;
    }

    public boolean purchaseProduct(Long id, int amount) {
        Product product = productRepository.findById(id).get();
        if (product.isProductEnough(amount)) {
            productRepository.purchaseProductById(id, amount);
            return true;
        }
        return false;
    }

    public Product selectProduct(Long id) {
        return productRepository.findById(id).get();
    }

    public Page<Product> selectAllProducts(Pageable pageable){
        return productRepository.findAll(pageable);
    }

    public void DeleteProduct(Long id){
        productRepository.deleteById(id);
    }

    public Category findCategory(String categoryName) {
        Optional<Category> category = categoryRepository.findByCategoryName(categoryName);
        if (category.isPresent()) {
            return category.get();
        }
        return categoryRepository.save(new Category(categoryName));
    }
}
