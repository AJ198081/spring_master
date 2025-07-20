import {useProductStore} from "../../store/ProductStore.tsx";
import {useCallback, useEffect, useState} from "react";
import {addNewCategory, getAvailableCategories} from "../../services/ProductService.ts";
import {toast, ToastContainer} from "react-toastify";

interface CategorySelectorProps {
    selectedCategory: string;
    setSelectedCategory: (value: string) => void;
}

export const CategorySelector = ({selectedCategory, setSelectedCategory}: CategorySelectorProps) => {

    const availableCategories = useProductStore(state => state.productCategories);
    const setAvailableCategories = useProductStore(state => state.setProductCategories);
    const [newCategory, setNewCategory] = useState<string>("");
    const [isNewCategory, setIsNewCategory] = useState(false);

    const categoriesGetter = useCallback(() => {
        getAvailableCategories()
            .then(availableCategories => {
                setAvailableCategories(availableCategories);
            })
            .catch(error => {
                    toast.error(`Exception ${error.response?.data?.data}`);
                }
            )
    }, [setAvailableCategories])

    useEffect(() => {
            categoriesGetter();
    }, [categoriesGetter])

    const handleAddCategory = () => {
        if (isNewCategory && newCategory.trim()) {
            addNewCategory(newCategory)
                .then(newCategory => {
                    categoriesGetter();
                    setSelectedCategory(newCategory.name);
                    setIsNewCategory(false);
                    setSelectedCategory(newCategory.name);
                    toast.success(`Category "${newCategory.name}" added successfully!`);
                })
                .catch(error => {
                    toast.error(`Exception ${error.response?.data?.data}`);
                })
        }
    }

    const handleCancelNewCategory = () => {
        setIsNewCategory(false);
        setNewCategory("");
        setSelectedCategory("new");
    }

    return (
        <div>
            <ToastContainer/>
            {isNewCategory ? (
                <div className={``}>
                    <div className="input-group">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Enter new category name"
                            value={newCategory}
                            onChange={(e) => setNewCategory(e.target.value)}
                        />
                        <button
                            className="btn btn-primary"
                            type="button"
                            onClick={handleAddCategory}
                            disabled={!newCategory.trim()}
                        >
                            Add
                        </button>
                        <button
                            className="btn btn-secondary"
                            type="button"
                            onClick={handleCancelNewCategory}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            ) : (
                <select
                    id={"category"}
                    value={selectedCategory || ''}
                    required={true}
                    onChange={e => {
                        if (e.target.value === 'new') {
                            setIsNewCategory(true);
                            setNewCategory('');
                        } else {
                            setSelectedCategory(e.target.value);
                        }
                    }}
                    className={"form-select"}
                >
                    <option value={''}>Select a category</option>
                    {availableCategories.map(((category) => (
                        <option
                            key={category}
                            value={category}
                        >
                            {category}
                        </option>
                    )))}
                    <option value={'new'}>Add New Category</option>
                </select>
            )}
        </div>
    )
}
