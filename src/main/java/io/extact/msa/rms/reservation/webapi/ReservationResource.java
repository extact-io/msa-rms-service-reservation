package io.extact.msa.rms.reservation.webapi;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;

import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.ParameterIn;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirement;
import org.eclipse.microprofile.openapi.annotations.security.SecurityRequirements;

import io.extact.msa.rms.platform.fw.domain.constraint.RmsId;
import io.extact.msa.rms.platform.fw.exception.BusinessFlowException;
import io.extact.msa.rms.reservation.webapi.dto.AddReservationEventDto;
import io.extact.msa.rms.reservation.webapi.dto.ReservationResourceDto;

public interface ReservationResource {
    /*
     * NOTE:
     * @SecurityRequirementSetで本来はRmsHeaderAuthn and RmsHeaderAuthzにすべきだがsmallrye-open-apiがバグ？で未サポートのため
     * @SecurityRequirementsで定義している。MicroProfile OpenAPI 3.1.0では対応されるハズ
     * see: https://github.com/smallrye/smallrye-open-api/pull/1097
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "getAll", summary = "予約の全件を取得する", description = "登録されているすべての予約を取得する")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    List<ReservationResourceDto> getAll();

    @GET
    @Path("/item/{itemId}/startdate/{startDate}")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "findByRentalItemAndStartDate", summary = "指定されたレンタル品と利用開始日で予約を検索する", description = "指定されたレンタル品と利用開始日に一致する予約を検索する")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @Parameter(name = "startDate", description = "利用開始日", in = ParameterIn.PATH, required = true, schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    List<ReservationResourceDto> findByRentalItemAndStartDate(@RmsId @PathParam("itemId") Integer itemId, @NotNull @PathParam("startDate") LocalDate startDate);

    @GET
    @Path("/reserver/{reserverId}")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "findByReserverId", summary = "指定されたユーザが予約者の予約を検索する", description = "指定されたユーザが予約者の予約を検索する")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "reserverId", description = "ユーザID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    List<ReservationResourceDto> findByReserverId(@RmsId @PathParam("reserverId") Integer reserverId);

    @GET
    @Path("/item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "findByRentalItemId", summary = "指定されたレンタル品に対する予約を検索する", description = "指定されたレンタル品に対する予約を検索する")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "rentalItemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    List<ReservationResourceDto> findByRentalItemId(@RmsId @PathParam("itemId") Integer itemId);

    @GET
    @Path("/item/overlapped")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "findOverlappedReservations", summary = "指定された期間と被る予約を検索する", description = "指定された期間と予約されている期間が被っているものを対象にする")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "from", description = "利用開始日", in = ParameterIn.PATH, required = true, schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    @Parameter(name = "to", description = "利用終了日", in = ParameterIn.PATH, required = true, schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    List<ReservationResourceDto> findOverlappedReservations(@NotNull @QueryParam("from") LocalDateTime from,
            @NotNull @QueryParam("to") LocalDateTime to);

    @GET
    @Path("/item/{itemId}/overlapped")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "findOverlappedReservation", summary = "指定された期間で予約されているレンタル品の予約を取得する", description = "指定された期間と予約されている期間が被っているものを対象にする。なお、該当なしはnullに相当する204(NoContent)を返す")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "itemId", description = "レンタル品ID", in = ParameterIn.PATH, required = true)
    @Parameter(name = "from", description = "利用開始日", in = ParameterIn.PATH, required = true, schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    @Parameter(name = "to", description = "利用終了日", in = ParameterIn.PATH, required = true, schema = @Schema(implementation = String.class, example = "20201230", format = "yyyyMMdd"))
    @APIResponse(responseCode = "200", description = "検索結果", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.ARRAY, implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "204", ref = "#/components/responses/NoContent")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    ReservationResourceDto findOverlappedReservation(@RmsId @PathParam("itemId") Integer itemId,
            @NotNull @QueryParam("from") LocalDateTime from,
            @NotNull @QueryParam("to") LocalDateTime to);

    @GET
    @Path("/has-item/{itemId}")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "hasRentalItemWith", summary = "指定されたレンタル品に対する予約があるかを返す")
    @SecurityRequirement(name = "RmsJwtAuth")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @APIResponse(responseCode = "200", description = "ある場合はtrueを返す", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.BOOLEAN, implementation = Boolean.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    boolean hasRentalItemWith(@RmsId @PathParam("itemId") Integer itemId);

    @GET
    @Path("/has-user/{userId}")
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "hasUserAccountWith", summary = "指定されたユーザの予約があるかを返す")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "userId", description = "ユーザID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "ある場合はtrueを返す", content = @Content(mediaType = "application/json", schema = @Schema(type = SchemaType.BOOLEAN, implementation = Boolean.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    boolean hasUserAccountWith(@RmsId @PathParam("userId") Integer userId);

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "add", summary = "レンタル品を予約する", description = "予約対象のレンタル品が存在しない場合は404を予定期間に別の予約が既に入っている場合は409を返す")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "dto", description = "登録内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = AddReservationEventDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "409", ref = "#/components/responses/DataDupricate")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    ReservationResourceDto add(@Valid AddReservationEventDto dto) throws BusinessFlowException;

    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    //--- for OpenAPI
    @Operation(operationId = "update", summary = "予約を更新する", description = "依頼された予約を更新する")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "dto", description = "更新内容", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "200", description = "登録成功", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservationResourceDto.class)))
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    ReservationResourceDto update(@Valid ReservationResourceDto dto);

    @DELETE
    @Path("/{reservationId}")
    //--- for OpenAPI
    @Operation(operationId = "delete", summary = "予約を削除する", description = "予約を削除する")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "reservationId", description = "予約ID", in = ParameterIn.PATH, required = true)
    @APIResponse(responseCode = "200", description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    void delete(@RmsId @PathParam("reservationId") Integer reservationId) throws BusinessFlowException;

    @DELETE
    @Path("cancel")
    //--- for OpenAPI
    @Operation(operationId = "cancel", summary = "予約をキャンセルする", description = "依頼された予約IDに対する予約をキャンセルする。予約のキャンセルは予約した人しか行えない。"
            + "他の人が予約キャンセルを行った場合は禁止操作としてエラーにする")
    @SecurityRequirements({@SecurityRequirement(name = "RmsHeaderAuthn"), @SecurityRequirement(name = "RmsHeaderAuthz")})
    @Parameter(name = "reservationId", description = "予約ID", in = ParameterIn.QUERY, required = true)
    @Parameter(name = "reserverId", description = "予約者ID", in = ParameterIn.QUERY, required = true)
    @APIResponse(responseCode = "200", description = "登録成功")
    @APIResponse(responseCode = "400", ref = "#/components/responses/ParameterError")
    @APIResponse(responseCode = "403", ref = "#/components/responses/Forbidden")
    @APIResponse(responseCode = "404", ref = "#/components/responses/UnknownData")
    @APIResponse(responseCode = "500", ref = "#/components/responses/ServerError")
    void cancel(@RmsId @QueryParam("reservationId") Integer reservationId,
            @RmsId @QueryParam("reserverId") Integer reserverId) throws BusinessFlowException;
}
