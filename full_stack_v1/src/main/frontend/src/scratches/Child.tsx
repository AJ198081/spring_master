import {MouseEvent, ReactNode, useRef} from "react";

export const Child = ({color}: { color: string }): ReactNode => {

    const divElement = useRef<HTMLDivElement | null>(null);

    function logClickOnDiv(event: MouseEvent<HTMLDivElement>) {
        event.preventDefault();
    }

    return <div onClick={logClickOnDiv} defaultValue={'test'} ref={divElement} >
        Hi there {color}!
    </div>
}

