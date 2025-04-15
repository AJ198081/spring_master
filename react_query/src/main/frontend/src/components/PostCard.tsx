import {Card, Em, Heading} from "@chakra-ui/react";
import {ReactNode} from "react";
import {User} from "../domain/types/User.ts";

interface PostCardProps {
    title: string,
    user: User,
    body: string,
}

export function PostCard({title, user, body}: PostCardProps): ReactNode {
    return (
        <Card.Root size="sm">
            <Card.Header>
                <Heading size="md">{title}</Heading>
            </Card.Header>
            <Card.Body color="fg.muted">
                {body}
            </Card.Body>
            <Card.Footer>
                <Em>`Posted by - ${user.username}`</Em>
            </Card.Footer>
        </Card.Root>
    )
}