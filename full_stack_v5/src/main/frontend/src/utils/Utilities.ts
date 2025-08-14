import _ from "lodash";

export function getObjectPatch<T extends object>(
    modified: Partial<T>,
    original: Partial<T>
): Partial<T> {
    return _.transform(
        modified,
        (result: Partial<T>, value, keyStr) => {
            const key = keyStr as keyof T;

            if (!_.isEqual(value, original[key])) {
                if (_.isPlainObject(value)
                    && _.isPlainObject(original[key])) {
                    result[key] = getObjectPatch(
                        value as Partial<T[keyof T]>,
                        original[key]!
                    ) as T[keyof T];
                } else {
                    result[key] = value as T[keyof T];
                }
            }
        },
        {} as Partial<T>
    );
}


const originalProduct = {
    id: 1,
    name: "Product 1",
    description: "Description 1",
    price: 100,
    category: "Category 1",
    brand: "Brand 1",
    images: ["image1.jpg", "image2.jpg"],
    quantity: 10,
    rating: 4.5,

}

const modifiedProduct = {
    id: 1,
    name: "Product 1",
    description: "Description 1",
    price: 100,
    category: "Category 2",
    images: ["image1.jpg", "image3.jpg"],
    brand: "Brand 2",
}

const deepDifference = getObjectPatch(modifiedProduct, originalProduct);
console.log(deepDifference);
