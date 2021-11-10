package kpi.diploma.ovcharenko.util;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author Aleksandr Ovcharenko
 */
@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageItem {

    private PageItemType pageItemType;

    private int index;

    private boolean active;

}
