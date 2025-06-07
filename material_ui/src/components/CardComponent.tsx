import {Avatar, Button, Card, CardActionArea, CardActions, CardContent, CardHeader, CardMedia, Typography} from "@mui/material";

export const CardComponent = () => {
    const catImage = '/cat.png';
    return (
        <Card
            sx={{
                maxWidth: 345,
                minWidth: 120,
                minHeight: 400,
                marginY: 2
            }}
            elevation={3}
            raised={true}
            // component={'div'}
        >

            <CardHeader
                avatar={<Avatar>Z</Avatar>}
                title="Hello"
                subheader="World"
            />

            <CardActionArea onClick={() => alert('Reet stop disturbing Dada, he is busy!!')}>
                <CardMedia
                    component="img"
                    height="240"
                    image={catImage}
                />
            </CardActionArea>
            <CardContent>
                <Typography
                    variant="body2"
                    color="text.secondary"
                >
                    Image of a cat, that can be clicked.
                </Typography>
            </CardContent>
            <CardActions>
                <Button
                    size="small"
                    variant={'contained'}
                    color={'secondary'}
                >Learn More</Button>
                <Button
                    size="small"
                    variant={'contained'}
                    color={'info'}
                >Share</Button>
            </CardActions>
        </Card>
    );
}