import {useEffect, useState} from "react";
import {AxiosInstance} from "../../services/api";
import {DataGrid} from "@mui/x-data-grid";
import {Blocks} from "react-loader-spinner";
import toast from "react-hot-toast";
import {Errors} from "../Errors.js";
import moment from "moment";
import {auditLogscolumn} from "../../utils/tableColumn.js";


export const AdminAuditLogs = () => {

    const [auditLogs, setAuditLogs] = useState([]);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    const fetchAuditLogs = async () => {
        setLoading(true);
        try {
            const response = await AxiosInstance.get("/audit");
            setAuditLogs(response.data);
        } catch (err) {
            setError(err?.response?.data?.message);
            toast.error("Error fetching audit logs");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchAuditLogs();
    }, []);

    const rows = auditLogs.map((item) => {
        //format the time bu using moment npm package

        const formattedDate = moment(item.timestamp).format(
            "MMMM DD, YYYY, hh:mm A"
        );

        //set the data for each rows in the table according to the field name in columns
        //Example: username is the keyword in row it should matche with the field name in column so that the data will show on that column dynamically
        return {
            id: item.id,
            noteId: item.noteId,
            actions: item.action,
            username: item.username,
            timestamp: formattedDate,
            noteid: item.noteId,
            note: item.noteContent,
        };
    });

    if (error) {
        return <Errors message={error}/>;
    }


    return (
        <div className="p-4">
            <div className="py-4">
                <h1 className="text-center text-2xl font-bold text-slate-800 uppercase">
                    Audit Logs
                </h1>
            </div>
            {loading ? (
                <>
                    {" "}
                    <div className="flex  flex-col justify-center items-center  h-72">
            <span>
              <Blocks
                  height="70"
                  width="70"
                  color="#4fa94d"
                  ariaLabel="blocks-loading"
                  wrapperStyle={{}}
                  wrapperClass="blocks-wrapper"
                  visible={true}
              />
            </span>
                        <span>Please wait...</span>
                    </div>
                </>
            ) : (
                <>
                    {" "}
                    <div className="overflow-x-auto w-full mx-auto">
                        <DataGrid
                            className="w-fit mx-auto px-0"
                            rows={rows}
                            columns={auditLogscolumn}
                            initialState={{
                                pagination: {
                                    paginationModel: {
                                        pageSize: 6,
                                    },
                                },
                            }}
                            pageSizeOptions={[6]}
                            disableRowSelectionOnClick
                            disableColumnResize
                        />
                    </div>
                </>
            )}
        </div>
    );
}