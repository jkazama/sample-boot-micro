package sample.context.orm;

import java.util.*;

import lombok.Getter;
import sample.context.Dto;

/**
 * ページング一覧を表現します。
 * 
 * @param <T> 結果オブジェクト(一覧の要素)
 */
@Getter
public class PagingList<T> implements Dto {
    private static final long serialVersionUID = 1L;

    public PagingList() {
        this(new ArrayList<>(), new Pagination());
    }
    
    public PagingList(List<T> list, Pagination page) {
        this.list = list;
        this.page = page;
    }
    
    private List<T> list;
    private Pagination page;
    
}
