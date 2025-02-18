import React, {FC} from 'react';

interface CategorySelectProps {
    valueList: string[];
    label: string;
    selectLabel?: string;
    value: string;
    onChange: (event: React.ChangeEvent<HTMLSelectElement>) => void;
    onBlur: (event: React.FocusEvent<HTMLSelectElement>) => void;
    error?: string;
    touched?: boolean;
}

export const CategorySelect: FC<CategorySelectProps> = ({
                                                            valueList,
                                                            label,
                                                            selectLabel = "Select category",
                                                            value,
                                                            onChange,
                                                            onBlur,
                                                            error,
                                                            touched
                                                        }) => (
    <div className="mb-3">

        <label htmlFor="category" className="form-label">{label}</label>
        <select
            id="category"
            className={`form-select ${error && touched ? 'is-invalid' : ''}`}
            value={value}
            onChange={onChange}
            onBlur={onBlur}
            aria-label="Expense category"
        >
            <option value="">{selectLabel}</option>
            {
                valueList.map(category => <option key={category} value={category}>{category}</option>)
            }
        </select>
        {touched && error && <div className="invalid-feedback">{error}</div>}
    </div>);