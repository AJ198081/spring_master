import {ReactNode, MouseEvent, useRef} from "react";

export const Child = ({color}: { color: string }): ReactNode => {

    const divElement = useRef<HTMLDivElement | null>(null);

    function logClickOnDiv(event: MouseEvent<HTMLDivElement>) {
        event.preventDefault();
        console.log(`Clicked on div ${event.target}`);
    }

    return <div onClick={logClickOnDiv} defaultValue={'test'} ref={divElement} >
        Hi there {color}!
    </div>
}

