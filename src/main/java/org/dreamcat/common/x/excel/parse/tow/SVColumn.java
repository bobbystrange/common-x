package org.dreamcat.common.x.excel.parse.tow;

import lombok.Data;

import java.util.Map;

/**
 * Create by tuke on 2020/8/27
 */
@Data
public class SVColumn<S> {
    public S scalar;
    public Map<String, String> map;
}
