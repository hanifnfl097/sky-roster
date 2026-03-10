"use client";

import { usePathname } from "next/navigation";

const ROUTE_LABELS: Record<string, string> = {
    "/": "Dashboard",
    "/crew": "Crew Members",
    "/roster": "Roster Board",
    "/operations": "Flight Board",
    "/reports": "Voyage Reports",
    "/notifications": "Notifications",
    "/audit": "Audit Log",
    "/settings": "Settings",
};

export default function Header(): React.JSX.Element {
    const pathname = usePathname();

    const getBreadcrumb = (): string[] => {
        const parts = pathname.split("/").filter(Boolean);
        if (parts.length === 0) return ["Dashboard"];

        const crumbs: string[] = [];
        let currentPath = "";
        for (const part of parts) {
            currentPath += `/${part}`;
            crumbs.push(ROUTE_LABELS[currentPath] || part);
        }
        return crumbs;
    };

    const breadcrumb = getBreadcrumb();
    const pageTitle = breadcrumb[breadcrumb.length - 1];

    return (
        <header
            id="page-header"
            style={{
                height: "var(--header-height)",
                background: "rgba(255,255,255,0.85)",
                backdropFilter: "blur(12px)",
                borderBottom: "1px solid var(--border)",
                display: "flex",
                alignItems: "center",
                justifyContent: "space-between",
                padding: "0 32px",
                position: "sticky",
                top: 0,
                zIndex: 30,
            }}
        >
            <div>
                {/* Breadcrumb */}
                <div style={{ display: "flex", alignItems: "center", gap: 8, fontSize: 12, color: "var(--slate-400)" }}>
                    {breadcrumb.map((crumb, idx) => (
                        <span key={idx} style={{ display: "flex", alignItems: "center", gap: 8 }}>
                            {idx > 0 && <span>›</span>}
                            <span style={idx === breadcrumb.length - 1 ? { color: "var(--sky-600)", fontWeight: 600 } : {}}>
                                {crumb}
                            </span>
                        </span>
                    ))}
                </div>
                {/* Page Title */}
                <h1 style={{ fontSize: 20, fontWeight: 700, color: "var(--slate-900)", margin: 0, lineHeight: 1.2 }}>
                    {pageTitle}
                </h1>
            </div>

            {/* User Area */}
            <div style={{ display: "flex", alignItems: "center", gap: 16 }}>
                <button
                    style={{
                        padding: "8px 12px",
                        borderRadius: 8,
                        background: "transparent",
                        border: "1px solid var(--border)",
                        cursor: "pointer",
                        fontSize: 18,
                        position: "relative",
                    }}
                    aria-label="Notifications"
                >
                    🔔
                    <span
                        style={{
                            position: "absolute",
                            top: 4,
                            right: 4,
                            width: 8,
                            height: 8,
                            background: "var(--red-500)",
                            borderRadius: "50%",
                            border: "2px solid white",
                        }}
                    />
                </button>
                <div
                    style={{
                        display: "flex",
                        alignItems: "center",
                        gap: 10,
                        padding: "6px 12px",
                        borderRadius: 8,
                        background: "var(--slate-50)",
                        border: "1px solid var(--border)",
                    }}
                >
                    <div
                        style={{
                            width: 32,
                            height: 32,
                            borderRadius: 8,
                            background: "linear-gradient(135deg, var(--sky-400), var(--sky-600))",
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            color: "white",
                            fontSize: 14,
                            fontWeight: 700,
                        }}
                    >
                        A
                    </div>
                    <div>
                        <div style={{ fontWeight: 600, fontSize: 13, color: "var(--slate-800)" }}>Admin</div>
                        <div style={{ fontSize: 11, color: "var(--slate-400)" }}>SUPER_ADMIN</div>
                    </div>
                </div>
            </div>
        </header>
    );
}
