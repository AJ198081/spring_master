import {Box} from "@chakra-ui/react";
import {Route, Routes} from "react-router-dom";
import {HomePage} from "@/pages/HomePage.tsx";
import {CreatePage} from "@/pages/CreatePage.tsx";
import {Navbar} from "@/components/Navbar.tsx";
import {useColorModeValue} from "@/components/ui/color-mode.tsx";

export function App() {

  return (
      <Box minH={"100vh"} bg={useColorModeValue("gray.200", "gray.900")}>
          <Navbar />
          <Routes>
              <Route path="/" element={<HomePage />}/>
              <Route path={"/create"} element={<CreatePage />} />
          </Routes>
      </Box>
  )
}