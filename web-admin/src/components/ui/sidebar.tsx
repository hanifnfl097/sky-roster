"use client";

import Link from "next/link";
import { usePathname } from "next/navigation";

const NAV_SECTIONS = [
    {
        label: "MAIN",
        items: [
            { name: "Dashboard", href: "/", icon: "📊" },
            { name: "Crew Members", href: "/crew", icon: "👨‍✈️" },
        ],
    },
    {
        label: "OPERATIONS",
        items: [
            { name: "Roster Board", href: "/roster", icon: "📅" },
            { name: "Flight Board", href: "/operations", icon: "✈️" },
            { name: "Voyage Reports", href: "/reports", icon: "📋" },
        ],
    },
    {
        label: "SYSTEM",
        items: [
            { name: "Notifications", href: "/notifications", icon: "🔔" },
            { name: "Audit Log", href: "/audit", icon: "🔐" },
            { name: "Settings", href: "/settings", icon: "⚙️" },
        ],
    },
];

export default function Sidebar(): React.JSX.Element {
    const pathname = usePathname();

    return (
        <aside
            id="sidebar"
            style={{
                width: "var(--sidebar-width)",
                minHeight: "100vh",
                background: "linear-gradient(180deg, var(--slate-900) 0%, var(--slate-950) 100%)",
                color: "white",
                padding: "0",
                position: "fixed",
                top: 0,
                left: 0,
                zIndex: 40,
                display: "flex",
                flexDirection: "column",
                overflowY: "auto",
            }}
        >
            {/* Brand */}
            <div
                style={{
                    padding: "24px 20px",
                    borderBottom: "1px solid rgba(255,255,255,0.08)",
                }}
            >
                <div style={{ display: "flex", alignItems: "center", gap: "12px" }}>
                    <div
                        style={{
                            width: 40,
                            height: 40,
                            background: "linear-gradient(135deg, var(--sky-400), var(--sky-600))",
                            borderRadius: 10,
                            display: "flex",
                            alignItems: "center",
                            justifyContent: "center",
                            fontSize: 20,
                            fontWeight: 700,
                            boxShadow: "0 4px 12px rgba(14,165,233,0.3)",
                        }}
                    >
                        ✈
                    </div>
                    <div>
                        <div style={{ fontWeight: 700, fontSize: 18, letterSpacing: "-0.02em" }}>
                            SkyRoster
                        </div>
                        <div style={{ fontSize: 11, color: "var(--slate-400)", letterSpacing: "0.04em" }}>
                            CREW MANAGEMENT
                        </div>
                    </div>
                </div>
            </div>

            {/* Navigation */}
            <nav style={{ flex: 1, padding: "16px 12px" }}>
                {NAV_SECTIONS.map((section) => (
                    <div key={section.label} style={{ marginBottom: 24 }}>
                        <div
                            style={{
                                fontSize: 10,
                                fontWeight: 700,
                                color: "var(--slate-500)",
                                padding: "0 10px",
                                marginBottom: 8,
                                letterSpacing: "0.1em",
                            }}
                        >
                            {section.label}
                        </div>
                        {section.items.map((item) => {
                            const isActive = item.href === "/"
                                ? pathname === "/"
                                : pathname.startsWith(item.href);

                            return (
                                <Link
                                    key={item.href}
                                    href={item.href}
                                    style={{
                                        display: "flex",
                                        alignItems: "center",
                                        gap: 12,
                                        padding: "10px 12px",
                                        borderRadius: 8,
                                        fontSize: 14,
                                        fontWeight: isActive ? 600 : 400,
                                        color: isActive ? "white" : "var(--slate-400)",
                                        background: isActive
                                            ? "linear-gradient(135deg, rgba(14,165,233,0.2), rgba(14,165,233,0.1))"
                                            : "transparent",
                                        textDecoration: "none",
                                        transition: "all 0.15s ease",
                                        marginBottom: 2,
                                        border: isActive ? "1px solid rgba(14,165,233,0.2)" : "1px solid transparent",
                                    }}
                                >
                                    <span style={{ fontSize: 18 }}>{item.icon}</span>
                                    <span>{item.name}</span>
                                </Link>
                            );
                        })}
                    </div>
                ))}
            </nav>

            {/* Footer */}
            <div
                style={{
                    padding: "16px 20px",
                    borderTop: "1px solid rgba(255,255,255,0.06)",
                    fontSize: 11,
                    color: "var(--slate-500)",
                }}
            >
                SkyRoster v1.0.0 · CASR 121
            </div>
        </aside>
    );
}
