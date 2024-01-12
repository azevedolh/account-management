package br.com.teste.accountmanagement.specification;

import br.com.teste.accountmanagement.model.Customer;
import org.springframework.data.jpa.domain.Specification;

public class CustomerSpecifications {

    public static Specification<Customer> generateDinamic(String name, String document) {
        Specification<Customer> specification = Specification.where(null);

        if (name != null) {
            specification = specification.and(nameContains(name));
        }

        if (document != null) {
            specification = specification.and(documentEquals(document));
        }

        return specification;
    }

    private static Specification<Customer> documentEquals(String document) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.equal(root.get("document"), document);
    }

    public static Specification<Customer> nameContains(String name) {
        return (root, criteriaQuery, criteriaBuilder) -> criteriaBuilder.like(criteriaBuilder.upper(root.get("name")), "%" + name.toUpperCase() + "%");
    }
}
