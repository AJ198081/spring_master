import {Route, Routes} from "react-router-dom";
import {AdminAreaSidebar} from "./AuditLogs/AdminAreaSidebar";
import {UserList} from "./AuditLogs/UserList";
import {UserDetails} from "./AuditLogs/UserDetails";
import {AuditLogsDetails} from "./AuditLogs/AuditLogsDetails";
import {AdminAuditLogs} from "./AuditLogs/AdminAuditLogs";
import {useApiContext} from "../hooks/ApiContextHook.ts";

export const Admin = () => {
    // Access the openSidebar hook using the useMyContext hook from the ContextProvider
    const {openSidebar} = useApiContext();

    return (
        <div className="flex">
            <AdminAreaSidebar />
            <div
                className={`transition-all overflow-hidden flex-1 duration-150 w-full min-h-[calc(100vh-74px)] ${
                    openSidebar ? "lg:ml-52 ml-12" : "ml-12"
                }`}
            >
                <Routes>
                    <Route path="audit-logs" element={<AdminAuditLogs />} />
                    <Route path="audit-logs/:noteId" element={<AuditLogsDetails />} />
                    <Route path="users" element={<UserList />} />
                    <Route path="users/:userId" element={<UserDetails />} />
                    {/* Add other routes as necessary */}
                </Routes>
            </div>
        </div>
    );
};

