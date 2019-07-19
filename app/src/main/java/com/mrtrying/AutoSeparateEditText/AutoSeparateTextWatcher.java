package com.mrtrying.AutoSeparateEditText;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.widget.EditText;

import androidx.annotation.NonNull;

/**
 * Description :
 * PackageName : com.mrtrying.AutoSeparateEditText
 * Created by mrtrying on 2019/7/19 17:49.
 * e_mail : ztanzeyu@gmail.com
 */
public class AutoSeparateTextWatcher implements TextWatcher {
    /***/
    private StringBuffer mStringBuffer = new StringBuffer();
    /** 分割符 */
    private char separator = ' ';
    /** 分割符插入位置规则 */
    private int[] RULES = {3, 4, 4};
    /** 最大输入长度 */
    private int MAX_INPUT_LENGTH;
    /** EditText */
    private EditText editText;
    /** 最大输入长度InputFilter */
    private InputFilter.LengthFilter mLengthFilter;

    /**
     * @param editText 目标EditText
     */
    public AutoSeparateTextWatcher(@NonNull EditText editText) {
        this.editText = editText;
        //更新输入最大长度
        setupMaxInputLength();
    }

    /**
     * 设置分割规则
     * @param RULES 分割规则数组
     *              例如：138 383 81438的分割数组是{3,3,5}
     */
    public void setRULES(@NonNull int[] RULES) {
        this.RULES = RULES;
        setupMaxInputLength();
        String originalText = removeSpecialSeparator(editText, this.separator);
        if (!TextUtils.isEmpty(originalText)) {
            editText.setText(originalText);
            editText.setSelection(editText.getText().length());
        }
    }

    /**
     * 设置分割符
     * @param separator 分隔符，默认为空格
     */
    public void setSeparator(char separator) {
        String originalText = removeSpecialSeparator(editText, this.separator);
        this.separator = separator;
        if (!TextUtils.isEmpty(originalText)) {
            editText.setText(originalText);
            editText.setSelection(editText.getText().length());
        }
    }

    public char getSeparator() {
        return separator;
    }

    /** 更新最大输入长度 */
    private void setupMaxInputLength() {
        MAX_INPUT_LENGTH = RULES.length - 1;
        for (int value : RULES) {
            MAX_INPUT_LENGTH += value;
        }
        //更新LengthFilter
        InputFilter[] filters = editText.getFilters();
        if (filters.length > 0 && mLengthFilter != null) {
            //判断editText的InputFilter中是否已经包含mLengthFilter
            for (int i = 0; i < filters.length; i++) {
                InputFilter filter = filters[i];
                if (mLengthFilter == filter) {
                    mLengthFilter = new InputFilter.LengthFilter(MAX_INPUT_LENGTH);
                    filters[i] = mLengthFilter;
                    return;
                }
            }
        }
        addLengthFilter(filters);
    }

    /**
     * @param filters
     */
    private void addLengthFilter(InputFilter[] filters) {
        if (filters == null) {
            filters = new InputFilter[0];
        }
        InputFilter[] newFilters = new InputFilter[filters.length + 1];
        System.arraycopy(filters, 0, newFilters, 0, filters.length);
        mLengthFilter = new InputFilter.LengthFilter(MAX_INPUT_LENGTH);
        newFilters[newFilters.length - 1] = mLengthFilter;
        editText.setFilters(newFilters);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        if (!TextUtils.equals(s, mStringBuffer)) {
            //删除mStringBuffer中的文本
            mStringBuffer.delete(0, mStringBuffer.length());
            //添加分隔符
            mStringBuffer.append(handleText(s, RULES, separator));
            //删除多余字符
            if (mStringBuffer.length() > MAX_INPUT_LENGTH) {
                mStringBuffer.delete(MAX_INPUT_LENGTH, mStringBuffer.length());
            }
            final int currSelectStart = editText.getSelectionStart();
            //计算分隔符导致的光标offset
            int separatorOffset = calculateSeparatorOffset(s, mStringBuffer, currSelectStart);
            editText.setText(mStringBuffer);
            //计算并设置当前的selectStart位置
            int selectStart = currSelectStart + separatorOffset;
            if (selectStart < 0) {
                selectStart = 0;
            } else if (selectStart > mStringBuffer.length()) {
                selectStart = mStringBuffer.length();
            }
            editText.setSelection(selectStart);
        }
    }

    /**
     * 计算符号的offset
     *
     * @param before
     * @param after
     * @param selectionStart
     *
     * @return
     */
    private int calculateSeparatorOffset(@NonNull CharSequence before, @NonNull CharSequence after, int selectionStart) {
        int offset = 0;
        final int beforeLength = before.length();
        final int afterLength = after.length();
        final int length = afterLength > beforeLength ? beforeLength : afterLength;
        for (int i = 0; i < length; i++) {
            if (i >= selectionStart) {
                break;
            }
            char bc = before.charAt(i);
            char ac = after.charAt(i);
            if (bc == separator && ac != separator) {
                offset--;
            } else if (bc != separator && ac == separator) {
                offset++;
            }
        }
        return offset;
    }

    /**
     * @param s
     * @param rules
     * @param separator
     *
     * @return
     */
    public static String handleText(Editable s, int[] rules, char separator) {
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0, length = s.length(); i < length; i++) {
            char c = s.charAt(i);
            if (c != separator) {
                stringBuffer.append(c);
            }
            if (length != stringBuffer.length() && isSeparationPosition(rules, stringBuffer.length())) {
                stringBuffer.append(separator);
            }
        }
        return stringBuffer.toString();
    }

    /**
     * @param RULES
     * @param length
     *
     * @return
     */
    private static boolean isSeparationPosition(int[] RULES, int length) {
        if (RULES == null) {
            return false;
        }
        int standardPos = 0;
        int offset = 0;
        for (int pos : RULES) {
            standardPos += pos;
            if (length == standardPos + offset++) {
                return true;
            }
        }
        return false;
    }

    /**
     * @param editText
     * @param specialSeparator
     *
     * @return
     */
    public static String removeSpecialSeparator(EditText editText, char specialSeparator) {
        if (editText == null) {
            return null;
        }
        Editable text = editText.getText();
        return text == null ? null : text.toString().replace(String.valueOf(specialSeparator), "");
    }
}
