import {useProductStore} from "../../store/ProductStore.ts";
import {useCallback, useEffect, useState} from "react";
import {addNewBrand, getAvailableBrands} from "../../services/ProductService.ts";
import {toast} from "react-toastify";

interface BrandSelectorProps {
    selectedBrand: string;
    setSelectedBrand: (value: string) => void;
}

export const BrandSelector = ({selectedBrand, setSelectedBrand}: BrandSelectorProps) => {

    const availableBrands = useProductStore(state => state.productBrands);
    const setAvailableBrands = useProductStore(state => state.setProductBrands);
    const [newBrand, setNewBrand] = useState<string>(``);
    const [isNewBrand, setIsNewBrand] = useState(false);

    const brandsGetter = useCallback(() => {
        getAvailableBrands()
            .then(availableBrands => {
                setAvailableBrands(availableBrands);
            })
            .catch(error => {
                    toast.error(`Exception ${error.response?.data?.data}`);
                }
            )
    }, [setAvailableBrands])

    useEffect(() => {
        brandsGetter();
    }, [brandsGetter])

    const handleAddBrand = () => {
        if (isNewBrand && newBrand.trim()) {
            addNewBrand(newBrand)
                .then(newBrand => {
                    brandsGetter();
                    setSelectedBrand(newBrand.name);
                    setIsNewBrand(false);
                    setSelectedBrand(newBrand.name);
                    toast.success(`Brand "${newBrand.name}" added successfully!`);
                })
                .catch(error => {
                    toast.error(`Exception ${error.response?.data?.data}`);
                })
        }
    }

    const handleCancelNewBrand = () => {
        setIsNewBrand(false);
        setNewBrand('');
        setSelectedBrand(``);
    }

    return (
        <div>
            {isNewBrand ? (
                <div className={``}>
                    <div className="input-group">
                        <input
                            type="text"
                            className="form-control"
                            placeholder="Enter new brand name"
                            name="brand"
                            value={newBrand}
                            onChange={(e) => setNewBrand(e.target.value)}
                        />
                        <button 
                            className="btn btn-primary" 
                            type="button" 
                            onClick={handleAddBrand}
                            disabled={!newBrand.trim()}
                        >
                            Add
                        </button>
                        <button 
                            className="btn btn-secondary" 
                            type="button" 
                            onClick={handleCancelNewBrand}
                        >
                            Cancel
                        </button>
                    </div>
                </div>
            ) : (
                <select
                    id={"brand"}
                    value={selectedBrand || ''}
                    required={true}
                    onChange={e => {
                        if (e.target.value === 'new') {
                            setIsNewBrand(true);
                            setNewBrand('');
                        } else {
                            setSelectedBrand(e.target.value);
                        }
                    }}
                    className={"form-select"}>
                    <option value={''}>Select a brand</option>
                    {availableBrands.map(((brand) => (
                        <option key={brand} value={brand}>{brand}</option>
                    )))}
                    <option value={'new'}>Add New Brand</option>
                </select>
            )}
        </div>
    )
}
