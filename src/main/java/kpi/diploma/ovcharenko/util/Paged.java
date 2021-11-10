package kpi.diploma.ovcharenko.util;

import org.springframework.data.domain.Page;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Aleksandr Ovcharenko
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Paged<T> {

    private Page<T> page;

    private Paging paging;

}
