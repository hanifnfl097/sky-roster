/**
 * Crew API service functions.
 * Wraps apiClient with crew-specific endpoints.
 */

import { apiClient } from "./api-client";
import type {
    CrewMember,
    CrewDocument,
    CrewMemberResponse,
    CrewDocumentResponse,
} from "@/types";

/** Extended response type with documents included */
export interface CrewMemberDetail extends CrewMember {
    documents: CrewDocumentResponse[];
}

// Re-export response types for use by pages
export type CrewMemberResponse = CrewMember;

/** List crew with optional filters */
export async function listCrew(params?: {
    role?: string;
    status?: string;
    search?: string;
    page?: number;
    size?: number;
}): Promise<CrewMember[]> {
    const query = new URLSearchParams();
    if (params?.role) query.set("role", params.role);
    if (params?.status) query.set("status", params.status);
    if (params?.search) query.set("search", params.search);
    query.set("page", String(params?.page ?? 0));
    query.set("size", String(params?.size ?? 20));

    return apiClient<CrewMember[]>(`/crew?${query.toString()}`);
}

/** Get crew member by ID */
export async function getCrewById(id: string): Promise<CrewMember> {
    return apiClient<CrewMember>(`/crew/${id}`);
}

/** Register new crew member */
export async function registerCrew(data: {
    staffId: string;
    email: string;
    firstName: string;
    lastName: string;
    crewRole: string;
    baseStation: string;
}): Promise<CrewMember> {
    return apiClient<CrewMember>("/crew", {
        method: "POST",
        body: JSON.stringify(data),
    });
}

/** Update crew member */
export async function updateCrew(
    id: string,
    data: {
        staffId: string;
        email: string;
        firstName: string;
        lastName: string;
        crewRole: string;
        baseStation: string;
    }
): Promise<CrewMember> {
    return apiClient<CrewMember>(`/crew/${id}`, {
        method: "PUT",
        body: JSON.stringify(data),
    });
}

/** Ground or activate a crew member */
export async function changeCrewStatus(
    id: string,
    action: "GROUND" | "ACTIVATE",
    reason?: string
): Promise<void> {
    return apiClient<void>(`/crew/${id}/status`, {
        method: "PATCH",
        body: JSON.stringify({ action, reason }),
    });
}

/** Get crew documents */
export async function getCrewDocuments(
    crewId: string
): Promise<CrewDocumentResponse[]> {
    return apiClient<CrewDocumentResponse[]>(`/crew/${crewId}/documents`);
}

/** Add document to crew */
export async function addCrewDocument(
    crewId: string,
    data: {
        documentType: string;
        documentNumber?: string;
        issueDate: string;
        expiryDate: string;
        fileUrl?: string;
    }
): Promise<CrewDocumentResponse> {
    return apiClient<CrewDocumentResponse>(`/crew/${crewId}/documents`, {
        method: "POST",
        body: JSON.stringify(data),
    });
}

/** Get all expiring documents */
export async function getExpiringDocuments(
    days: number = 30
): Promise<CrewDocumentResponse[]> {
    return apiClient<CrewDocumentResponse[]>(
        `/crew/documents/expiring?days=${days}`
    );
}
