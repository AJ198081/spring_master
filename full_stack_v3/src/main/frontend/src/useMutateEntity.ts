import React from 'react';

export const useMutateEntity = <T, >() => {
    /**
     * Here event is React HTML Input Element Event
     */
    function updateEntity(event: React.ChangeEvent<HTMLInputElement>, entity: T) {
        const {name, value, type, checked} = event.target;

        return {
            ...entity,
            [name]: type === 'checkbox' ? checked : value
        }
    }
    
    return {
        updateEntity
    }
}