import {Route, BrowserRouter, Routes} from "react-router-dom";
import {ForgotPassword} from "./components/ForgotPassword";
import {Signup} from "./components/Signup";
import {AccessDenied} from "./components/AccessDenied";
import {Navbar} from "./components/Navbar";
import {Toaster} from "react-hot-toast";
import {ProtectedRoute} from "./components/ProtectedRoute";
import {LandingPage} from "./components/LandingPage";
import {Login} from "./components/Login.tsx";
import {ContactPage} from "./components/ContactPage";
import {AboutPage} from "./components/AboutPage";
import {ResetPassword} from "./components/ResetPassword";
import {NoteDetails} from "./components/NoteDetails";
import {CreateNote} from "./components/CreateNote";
import {Admin} from "./components/Admin";
import {UserProfile} from "./components/UserProfile";
import {OAuth2RedirectHandler} from "./components/OAuth2RedirectHandler";
import {NotFound} from "./components/NotFound";
import {Footer} from "./components/Footer";


function App() {


  return (

      <BrowserRouter>
          <Navbar />
          <Toaster position={"bottom-center"} reverseOrder={false} />
          <Routes>
             <Route path={"/"} index={true} element={<LandingPage />} />
              <Route path="/login" element={<Login />} />
              <Route path="/signup" element={<Signup />} />
              <Route path="/forgot-password" element={<ForgotPassword />} />
              <Route path="/contact" element={<ContactPage />} />
              <Route path="/about" element={<AboutPage />} />
              <Route path="/reset-password" element={<ResetPassword />} />
              <Route
                  path="/notes/:id"
                  element={
                      <ProtectedRoute adminPage={false}>
                          <NoteDetails />
                      </ProtectedRoute>
                  }
              />
              <Route
                  path="/create-note"
                  element={
                      <ProtectedRoute adminPage={false}>
                          <CreateNote />
                      </ProtectedRoute>
                  }
              />
              <Route path="/access-denied" element={<AccessDenied />} />
              <Route
                  path="/admin/*"
                  element={
                      <ProtectedRoute adminPage={true}>
                          <Admin />
                      </ProtectedRoute>
                  }
              />
              <Route
                  path="/profile"
                  element={
                      <ProtectedRoute adminPage={false}>
                          <UserProfile />
                      </ProtectedRoute>
                  }
              />
              <Route path="/oauth2/redirect" element={<OAuth2RedirectHandler />} />
              <Route path="*" element={<NotFound />} />
          </Routes>
         <Footer />
      </BrowserRouter>
  )
}

export default App
