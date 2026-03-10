"use client";

import { useParams, useRouter } from "next/navigation";
import Link from "next/link";
import { useState, useEffect } from "react";
import {
    getCrewById,
    getCrewDocuments,
    changeCrewStatus,
    addCrewDocument
} from "@/services/crew-service";
import type { CrewMember, CrewDocumentResponse } from "@/types";

const WARNING_THRESHOLD_DAYS = 30;
const CRITICAL_THRESHOLD_DAYS = 7;

function DocStatusBadge({ status, daysUntilExpiry }: { status: string; daysUntilExpiry: number }): React.JSX.Element {
    const classMap: Record<string, string> = {
        VALID: "badge-valid",
        EXPIRING_SOON: "badge-expiring-soon",
        EXPIRED: "badge-expired",
    };
    return (
        <span className={`badge ${classMap[status] || ""}`}>
            {status === "EXPIRED" ? `EXPIRED (${Math.abs(daysUntilExpiry)}d ago)` :
                status === "EXPIRING_SOON" ? `${daysUntilExpiry}d left` :
                    `${daysUntilExpiry}d left`}
        </span>
    );
}

export default function CrewDetailPage(): React.JSX.Element {
    const params = useParams();
    const router = useRouter();
    const crewId = params.id as string;

    const [crew, setCrew] = useState<CrewMember | null>(null);
    const [documents, setDocuments] = useState<CrewDocumentResponse[]>([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    const [showDocModal, setShowDocModal] = useState(false);
    const [isUpdatingStatus, setIsUpdatingStatus] = useState(false);

    const fetchData = async () => {
        try {
            setLoading(true);
            setError(null);

            const [crewData, docsData] = await Promise.all([
                getCrewById(crewId),
                getCrewDocuments(crewId)
            ]);

            setCrew(crewData);
            setDocuments(docsData);
        } catch (err: any) {
            setError(err.message || "Failed to load crew details.");
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        if (crewId) {
            fetchData();
        }
    }, [crewId]);

    const handleStatusChange = async (action: "GROUND" | "ACTIVATE") => {
        let reason = undefined;
        if (action === "GROUND") {
            const input = window.prompt("Enter reason for grounding:");
            if (input === null) return; // cancelled
            if (!input.trim()) {
                alert("Reason is required to ground a crew member.");
                return;
            }
            reason = input;
        }

        try {
            setIsUpdatingStatus(true);
            await changeCrewStatus(crewId, action, reason);
            fetchData(); // refresh data
        } catch (err: any) {
            alert(err.message || `Failed to ${action.toLowerCase()} crew`);
        } finally {
            setIsUpdatingStatus(false);
        }
    };

    if (loading) {
        return <div style={{ padding: 64, textAlign: "center", color: "var(--slate-500)" }}>Loading details...</div>;
    }

    if (error || !crew) {
        return (
            <div className="animate-fade-in-up" style={{ textAlign: "center", padding: 64 }}>
                <div style={{ fontSize: 48, marginBottom: 16 }}>🔍</div>
                <h2 style={{ color: "var(--slate-700)", marginBottom: 8 }}>Crew Not Found</h2>
                <p style={{ color: "var(--red-500)" }}>{error || "Could not find this crew member."}</p>
                <button onClick={() => router.push("/crew")} className="btn btn-primary" style={{ marginTop: 16, display: "inline-flex" }}>
                    ← Back to Crew List
                </button>
            </div>
        );
    }

    const statusColor = crew.status === "ACTIVE" ? "var(--green-500)" : crew.status === "GROUNDED" ? "var(--red-500)" : "var(--amber-500)";
    const statusBg = crew.status === "ACTIVE" ? "var(--green-100)" : crew.status === "GROUNDED" ? "var(--red-100)" : "var(--amber-100)";
    const criticalDocs = documents.filter(
        (d) => d.status === "EXPIRED" || d.daysUntilExpiry <= CRITICAL_THRESHOLD_DAYS
    );

    return (
        <div className="animate-fade-in-up">
            {/* Back nav */}
            <Link href="/crew" style={{ display: "inline-flex", alignItems: "center", gap: 6, color: "var(--slate-500)", fontSize: 14, textDecoration: "none", marginBottom: 20 }}>
                ← Back to Crew List
            </Link>

            {/* Profile Card */}
            <div className="glass-card" style={{ padding: 32, marginBottom: 24 }}>
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "flex-start", flexWrap: "wrap", gap: 20 }}>
                    {/* Left: Avatar + Info */}
                    <div style={{ display: "flex", gap: 24, alignItems: "center" }}>
                        <div
                            style={{
                                width: 80, height: 80, borderRadius: 20, flexShrink: 0,
                                background: `linear-gradient(135deg, ${crew.crewCategory === "FLIGHT_CREW" ? "var(--sky-400)" : "#a855f7"}, ${crew.crewCategory === "FLIGHT_CREW" ? "var(--sky-600)" : "#7c3aed"})`,
                                display: "flex", alignItems: "center", justifyContent: "center",
                                color: "white", fontSize: 28, fontWeight: 700,
                                boxShadow: "0 8px 24px rgba(0,0,0,0.1)",
                            }}
                        >
                            {crew.firstName[0]}{crew.lastName[0]}
                        </div>
                        <div>
                            <h2 style={{ fontSize: 24, fontWeight: 700, color: "var(--slate-900)", margin: 0 }}>
                                {crew.fullName || `${crew.firstName} ${crew.lastName}`}
                            </h2>
                            <div style={{ display: "flex", gap: 16, marginTop: 8, flexWrap: "wrap" }}>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>ID:</strong> {crew.staffId}
                                </span>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>Role:</strong> {crew.crewRole.replace("_", " ")}
                                </span>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>Base:</strong> {crew.baseStation}
                                </span>
                                <span style={{ fontSize: 13, color: "var(--slate-500)" }}>
                                    <strong>Category:</strong> {crew.crewCategory.replace("_", " ")}
                                </span>
                            </div>
                        </div>
                    </div>

                    {/* Right: Status + Actions */}
                    <div style={{ display: "flex", flexDirection: "column", alignItems: "flex-end", gap: 12 }}>
                        <span
                            style={{
                                padding: "8px 20px", borderRadius: 12, fontSize: 14, fontWeight: 700,
                                background: statusBg, color: statusColor,
                                border: `1px solid ${statusColor}30`,
                            }}
                        >
                            ● {crew.status.replace("_", " ")}
                        </span>
                        <div style={{ display: "flex", gap: 8 }}>
                            {crew.status === "ACTIVE" && (
                                <button
                                    className="btn btn-danger btn-sm"
                                    onClick={() => handleStatusChange("GROUND")}
                                    disabled={isUpdatingStatus}
                                >
                                    {isUpdatingStatus ? "..." : "🚫 Ground"}
                                </button>
                            )}
                            {crew.status === "GROUNDED" && (
                                <button
                                    className="btn btn-primary btn-sm"
                                    onClick={() => handleStatusChange("ACTIVATE")}
                                    disabled={isUpdatingStatus}
                                >
                                    {isUpdatingStatus ? "..." : "✅ Activate"}
                                </button>
                            )}
                            <button className="btn btn-outline btn-sm" disabled>✏️ Edit</button>
                        </div>
                    </div>
                </div>

                {/* Grounding Alert */}
                {crew.status === "GROUNDED" && crew.groundingReason && (
                    <div
                        style={{
                            marginTop: 20, padding: "14px 20px", borderRadius: 10,
                            background: "var(--red-100)", border: "1px solid rgba(239,68,68,0.2)",
                            display: "flex", alignItems: "center", gap: 12,
                        }}
                    >
                        <span style={{ fontSize: 20 }}>⚠️</span>
                        <div>
                            {crew.groundedAt && (
                                <div style={{ fontWeight: 600, color: "var(--red-700)", fontSize: 14 }}>
                                    Grounded Since {new Date(crew.groundedAt).toLocaleString()}
                                </div>
                            )}
                            <div style={{ color: "var(--red-700)", fontSize: 13, opacity: 0.85 }}>
                                Reason: {crew.groundingReason}
                            </div>
                        </div>
                    </div>
                )}
            </div>

            {/* Critical Alert */}
            {criticalDocs.length > 0 && (
                <div
                    className="animate-slide-in"
                    style={{
                        padding: "16px 20px", borderRadius: 12, marginBottom: 24,
                        background: "linear-gradient(135deg, rgba(239,68,68,0.06), rgba(245,158,11,0.06))",
                        border: "1px solid rgba(239,68,68,0.15)",
                        display: "flex", alignItems: "center", gap: 12,
                    }}
                >
                    <span style={{ fontSize: 24 }}>🚨</span>
                    <div>
                        <div style={{ fontWeight: 700, color: "var(--red-700)", fontSize: 14 }}>
                            {criticalDocs.length} Document{criticalDocs.length > 1 ? "s" : ""} Require Attention
                        </div>
                        <div style={{ fontSize: 13, color: "var(--slate-600)" }}>
                            {criticalDocs.map(d => d.documentType.replace("_", " ")).join(", ")} — renewal needed immediately.
                        </div>
                    </div>
                </div>
            )}

            {/* Documents Section */}
            <div className="glass-card" style={{ overflow: "hidden" }}>
                <div style={{ padding: "20px 24px", display: "flex", justifyContent: "space-between", alignItems: "center", borderBottom: "1px solid var(--border)" }}>
                    <h3 style={{ fontSize: 16, fontWeight: 700, color: "var(--slate-800)", margin: 0 }}>
                        Documents & Certificates ({documents.length})
                    </h3>
                    <button className="btn btn-primary btn-sm" onClick={() => setShowDocModal(true)}>
                        + Add Document
                    </button>
                </div>
                <table className="data-table">
                    <thead>
                        <tr>
                            <th>TYPE</th>
                            <th>NUMBER</th>
                            <th>ISSUE DATE</th>
                            <th>EXPIRY DATE</th>
                            <th>STATUS</th>
                        </tr>
                    </thead>
                    <tbody>
                        {documents.map((doc) => (
                            <tr key={doc.id}>
                                <td style={{ fontWeight: 600, color: "var(--slate-800)" }}>
                                    {doc.documentType.replace(/_/g, " ")}
                                </td>
                                <td style={{ fontFamily: "var(--font-mono, monospace)", fontSize: 13, color: "var(--slate-600)" }}>
                                    {doc.documentNumber || "N/A"}
                                </td>
                                <td>{doc.issueDate}</td>
                                <td>
                                    <span style={{
                                        fontWeight: doc.daysUntilExpiry <= CRITICAL_THRESHOLD_DAYS ? 700 : 400,
                                        color: doc.daysUntilExpiry <= CRITICAL_THRESHOLD_DAYS ? "var(--red-700)" : "var(--slate-700)",
                                    }}>
                                        {doc.expiryDate}
                                    </span>
                                </td>
                                <td><DocStatusBadge status={doc.status} daysUntilExpiry={doc.daysUntilExpiry} /></td>
                            </tr>
                        ))}
                        {documents.length === 0 && (
                            <tr>
                                <td colSpan={5} style={{ textAlign: "center", padding: 32, color: "var(--slate-400)" }}>
                                    No documents attached.
                                </td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Add Document Modal */}
            {showDocModal && (
                <AddDocumentModal
                    crewId={crewId}
                    onClose={() => setShowDocModal(false)}
                    onSuccess={() => {
                        setShowDocModal(false);
                        fetchData(); // reload docs
                    }}
                />
            )}
        </div>
    );
}

function AddDocumentModal({ crewId, onClose, onSuccess }: { crewId: string, onClose: () => void, onSuccess: () => void }): React.JSX.Element {
    const [formData, setFormData] = useState({
        documentType: "",
        documentNumber: "",
        issueDate: "",
        expiryDate: ""
    });
    const [isSubmitting, setIsSubmitting] = useState(false);
    const [error, setError] = useState<string | null>(null);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        try {
            setIsSubmitting(true);
            setError(null);
            await addCrewDocument(crewId, formData);
            onSuccess();
        } catch (err: any) {
            setError(err.message || "Failed to add document");
        } finally {
            setIsSubmitting(false);
        }
    };

    return (
        <div
            style={{
                position: "fixed", inset: 0, background: "rgba(0,0,0,0.4)",
                backdropFilter: "blur(4px)", display: "flex",
                alignItems: "center", justifyContent: "center", zIndex: 50,
            }}
            onClick={(e) => { if (e.target === e.currentTarget) onClose(); }}
        >
            <div
                className="animate-fade-in-up"
                style={{
                    background: "white", borderRadius: 16, padding: 32,
                    width: "100%", maxWidth: 480,
                    boxShadow: "0 24px 48px rgba(0,0,0,0.12)",
                }}
            >
                <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 24 }}>
                    <h2 style={{ fontSize: 20, fontWeight: 700, color: "var(--slate-900)", margin: 0 }}>
                        Add Document
                    </h2>
                    <button onClick={onClose} style={{ background: "none", border: "none", fontSize: 20, cursor: "pointer", color: "var(--slate-400)" }}>✕</button>
                </div>

                <form onSubmit={handleSubmit}>
                    <div style={{ display: "grid", gap: 16, marginBottom: 24 }}>
                        <div>
                            <label className="label" htmlFor="inp-doc-type">Document Type</label>
                            <select
                                id="inp-doc-type" required className="input"
                                value={formData.documentType} onChange={e => setFormData({ ...formData, documentType: e.target.value })}
                            >
                                <option value="">Select type...</option>
                                <option value="MEDEX">MEDEX</option>
                                <option value="ATPL">ATPL</option>
                                <option value="CPL">CPL</option>
                                <option value="INSTRUMENT_RATING">Instrument Rating</option>
                                <option value="SEP">SEP</option>
                                <option value="SIM_CHECK">Sim Check</option>
                                <option value="TYPE_RATING_CERT">Type Rating Certificate</option>
                            </select>
                        </div>
                        <div>
                            <label className="label" htmlFor="inp-doc-number">Document Number (Optional)</label>
                            <input
                                id="inp-doc-number" className="input" placeholder="ATPL-2026-001"
                                value={formData.documentNumber} onChange={e => setFormData({ ...formData, documentNumber: e.target.value })}
                            />
                        </div>
                        <div style={{ display: "grid", gridTemplateColumns: "1fr 1fr", gap: 12 }}>
                            <div>
                                <label className="label" htmlFor="inp-issue-date">Issue Date</label>
                                <input
                                    id="inp-issue-date" required className="input" type="date"
                                    value={formData.issueDate} onChange={e => setFormData({ ...formData, issueDate: e.target.value })}
                                />
                            </div>
                            <div>
                                <label className="label" htmlFor="inp-expiry-date">Expiry Date</label>
                                <input
                                    id="inp-expiry-date" required className="input" type="date"
                                    value={formData.expiryDate} onChange={e => setFormData({ ...formData, expiryDate: e.target.value })}
                                />
                            </div>
                        </div>
                    </div>

                    {error && (
                        <div style={{ padding: "12px", background: "var(--red-50)", color: "var(--red-700)", borderRadius: 6, marginBottom: 16, fontSize: 13 }}>
                            {error}
                        </div>
                    )}

                    <div style={{ display: "flex", gap: 12, justifyContent: "flex-end", marginTop: 28 }}>
                        <button type="button" className="btn btn-outline" onClick={onClose} disabled={isSubmitting}>Cancel</button>
                        <button type="submit" className="btn btn-primary" disabled={isSubmitting}>
                            {isSubmitting ? "Saving..." : "Save Document"}
                        </button>
                    </div>
                </form>
            </div>
        </div>
    );
}
