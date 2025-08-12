import _ from "lodash";

export function getObjectPatch<T extends object>(modified: Partial<T>, original: Partial<T>): Partial<T> {
    // @ts-ignore
    return _.transform(modified, (result, value: T[keyof T], key: keyof T) => {
    if (!_.isEqual(value, original[key])) {
      // @ts-ignore
        result[key] = (_.isObject(value) && _.isObject(original[key]))
          && !_.isArray(value) && !_.isArray(original[key])
          ? getObjectPatch(value, original[key])
          : value;
    }
  });
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
